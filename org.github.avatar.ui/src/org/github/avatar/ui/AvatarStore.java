/*******************************************************************************
 *  Copyright (c) 2011 Kevin Sawicki
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package org.github.avatar.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;

/**
 * Class that loads and stores avatars.
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public class AvatarStore implements Serializable, ISchedulingRule, IAvatarStore {

	/**
	 * URL
	 */
	public static final String URL = "http://www.gravatar.com/avatar/"; //$NON-NLS-1$

	/**
	 * HASH_REGEX
	 */
	public static final String HASH_REGEX = "[0-9a-f]{32}"; //$NON-NLS-1$

	/**
	 * HASH_PATTERN
	 */
	public static final Pattern HASH_PATTERN = Pattern.compile(HASH_REGEX); //$NON-NLS-1$

	/**
	 * HASH_LENGTH
	 */
	public static final int HASH_LENGTH = 32;

	/**
	 * HASH_ALGORITHM
	 */
	public static final String HASH_ALGORITHM = "MD5"; //$NON-NLS-1$

	/**
	 * TIMEOUT
	 */
	public static final int TIMEOUT = 30 * 1000;

	/**
	 * BUFFER_SIZE
	 */
	public static final int BUFFER_SIZE = 8192;

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 6084425297832914970L;

	/**
	 * Charset used for hashing
	 */
	public static final Charset CHARSET = Charset.forName("CP1252"); //$NON-NLS-1$

	private long lastRefresh = 0L;
	private String url;
	private Map<String, Avatar> avatars;

	/**
	 * Create avatar store
	 */
	public AvatarStore() {
		this(URL);
	}

	/**
	 * Create avatar store
	 * 
	 * @param url
	 */
	public AvatarStore(String url) {
		Assert.isNotNull(url, "Url cannot be null"); //$NON-NLS-1$
		// Ensure trailing slash
		if (!url.endsWith("/")) //$NON-NLS-1$
			url += "/"; //$NON-NLS-1$
		this.url = url;
		this.avatars = Collections
				.synchronizedMap(new HashMap<String, Avatar>());
	}

	/**
	 * @see org.github.avatar.ui.IAvatarStore#getRefreshTime()
	 */
	public long getRefreshTime() {
		return this.lastRefresh;
	}

	/**
	 * @see org.github.avatar.ui.IAvatarStore#containsAvatar(java.lang.String)
	 */
	public boolean containsAvatar(String hash) {
		return hash != null ? this.avatars.containsKey(hash) : false;
	}

	/**
	 * @see org.github.avatar.ui.IAvatarStore#scheduleRefresh()
	 */
	public IAvatarStore scheduleRefresh() {
		Job refresh = new Job(Messages.AvatarStore_RefreshJobName) {

			protected IStatus run(IProgressMonitor monitor) {
				refresh(monitor);
				return Status.OK_STATUS;
			}
		};
		refresh.setRule(this);
		refresh.schedule();
		return this;
	}

	/**
	 * @see org.github.avatar.ui.IAvatarStore#refresh(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IAvatarStore refresh(IProgressMonitor monitor) {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		String[] entries = null;
		synchronized (this.avatars) {
			entries = new String[this.avatars.size()];
			entries = this.avatars.keySet().toArray(entries);
		}
		monitor.beginTask("", entries.length); //$NON-NLS-1$
		for (String entry : entries) {
			monitor.setTaskName(MessageFormat.format(
					Messages.AvatarStore_LoadingAvatar, entry));
			try {
				loadAvatarByHash(entry);
			} catch (IOException ignore) {
			}
			monitor.worked(1);
		}
		monitor.done();
		this.lastRefresh = System.currentTimeMillis();
		return this;
	}

	/**
	 * Is the specified string a valid avatar hash?
	 * 
	 * @param hash
	 * @return true if valid hash, false otherwise
	 */
	public boolean isValidHash(String hash) {
		return hash != null && hash.length() == HASH_LENGTH
				&& HASH_PATTERN.matcher(hash).matches();
	}

	/**
	 * @see org.github.avatar.ui.IAvatarStore#loadAvatarByHash(java.lang.String,
	 *      org.github.avatar.ui.IAvatarCallback)
	 */
	public IAvatarStore loadAvatarByHash(final String hash,
			final IAvatarCallback callback) {
		String title = MessageFormat.format(Messages.AvatarStore_LoadingAvatar,
				hash);
		Job job = new Job(title) {

			protected IStatus run(IProgressMonitor monitor) {
				try {
					Avatar avatar = loadAvatarByHash(hash);
					if (avatar != null && callback != null)
						callback.loaded(avatar);
				} catch (IOException e) {
					if (callback != null)
						callback.error(e);
				}
				return Status.OK_STATUS;
			}
		};
		job.setRule(this);
		job.schedule();
		return this;
	}

	/**
	 * @see org.github.avatar.ui.IAvatarStore#loadAvatarByEmail(java.lang.String,
	 *      org.github.avatar.ui.IAvatarCallback)
	 */
	public IAvatarStore loadAvatarByEmail(String email, IAvatarCallback callback) {
		loadAvatarByHash(getHash(email), callback);
		return this;
	}

	/**
	 * @see org.github.avatar.ui.IAvatarStore#loadAvatarByHash(java.lang.String)
	 */
	public Avatar loadAvatarByHash(String hash) throws IOException {
		if (!isValidHash(hash))
			return null;

		Avatar avatar = null;
		HttpURLConnection connection = (HttpURLConnection) new URL(this.url
				+ hash).openConnection();
		connection.setConnectTimeout(TIMEOUT);
		connection.setUseCaches(false);
		connection.connect();

		if (connection.getResponseCode() != 200)
			return null;

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		InputStream input = connection.getInputStream();
		try {
			byte[] buffer = new byte[BUFFER_SIZE];
			int read = -1;
			while ((read = input.read(buffer)) != -1)
				output.write(buffer, 0, read);
		} finally {
			try {
				input.close();
			} catch (IOException ignore) {
			}
			try {
				output.close();
			} catch (IOException ignore) {
			}
		}
		avatar = new Avatar(hash, System.currentTimeMillis(),
				output.toByteArray());
		this.avatars.put(hash, avatar);
		return avatar;
	}

	/**
	 * @see org.github.avatar.ui.IAvatarStore#loadAvatarByEmail(java.lang.String)
	 */
	public Avatar loadAvatarByEmail(String email) throws IOException {
		return loadAvatarByHash(getHash(email));
	}

	/**
	 * @see org.github.avatar.ui.IAvatarStore#getAvatarByHash(java.lang.String)
	 */
	public Avatar getAvatarByHash(String hash) {
		return hash != null ? this.avatars.get(hash) : null;
	}

	private String digest(String value) {
		String hashed = null;
		try {
			byte[] digested = MessageDigest.getInstance(HASH_ALGORITHM).digest(
					value.getBytes(CHARSET));
			hashed = new BigInteger(1, digested).toString(16);
			int padding = HASH_LENGTH - hashed.length();
			if (padding > 0) {
				char[] zeros = new char[padding];
				Arrays.fill(zeros, '0');
				hashed = new String(zeros) + hashed;
			}
		} catch (NoSuchAlgorithmException e) {
			hashed = null;
		}
		return hashed;
	}

	/**
	 * Get avatar hash for specified e-mail address
	 * 
	 * @param email
	 * @return hash
	 */
	public String getHash(String email) {
		String hash = null;
		if (email != null) {
			email = email.trim().toLowerCase(Locale.US);
			if (email.length() > 0)
				hash = digest(email);
		}
		return hash;
	}

	/**
	 * Get hash for object by attempting to adapt it to a
	 * {@link IAvatarHashProvider} and fall back on {@link Object#toString()}
	 * value if adaptation fails.
	 * 
	 * @param element
	 * @return hash
	 */
	public String getAdaptedHash(Object element) {
		IAvatarHashProvider provider = null;
		if (element instanceof IAvatarHashProvider)
			provider = (IAvatarHashProvider) element;
		else if (element instanceof IAdaptable)
			provider = (IAvatarHashProvider) ((IAdaptable) element)
					.getAdapter(IAvatarHashProvider.class);
		if (provider != null)
			return provider.getAvatarHash();
		else {
			String potentialHash = element.toString();
			return isValidHash(potentialHash) ? potentialHash
					: getHash(potentialHash);
		}
	}

	/**
	 * @see org.github.avatar.ui.IAvatarStore#getAvatarByEmail(java.lang.String)
	 */
	public Avatar getAvatarByEmail(String email) {
		return getAvatarByHash(getHash(email));
	}

	/**
	 * @see org.eclipse.core.runtime.jobs.ISchedulingRule#contains(org.eclipse.core.runtime.jobs.ISchedulingRule)
	 */
	public boolean contains(ISchedulingRule rule) {
		return this == rule;
	}

	/**
	 * @see org.eclipse.core.runtime.jobs.ISchedulingRule#isConflicting(org.eclipse.core.runtime.jobs.ISchedulingRule)
	 */
	public boolean isConflicting(ISchedulingRule rule) {
		return this == rule;
	}
}
