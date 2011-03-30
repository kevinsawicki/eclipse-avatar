/**
 * 
 */
package org.github.avatar.ui;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;

/**
 * @author Kevin Sawicki (kevin@github.com)
 */
public class AvatarStore implements Serializable, ISchedulingRule {

	/**
	 * URL
	 */
	public static final String URL = "http://www.gravatar.com/avatar/"; //$NON-NLS-1$

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
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 6084425297832914970L;

	/**
	 * Charset used for hashing
	 */
	public static final Charset CHARSET = Charset.forName("CP1252"); //$NON-NLS-1$

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
		if (!url.endsWith("/")) { //$NON-NLS-1$
			url += "/"; //$NON-NLS-1$
		}
		this.url = url;
		this.avatars = Collections
				.synchronizedMap(new HashMap<String, Avatar>());
	}

	/**
	 * Does this store contain the specified avatar for the hash
	 * 
	 * @param hash
	 * @return true if store contains avatar, false otherwise
	 */
	public boolean containsAvatar(String hash) {
		return hash != null ? this.avatars.containsKey(hash) : false;
	}

	/**
	 * Dispose of all avatars in store
	 */
	public void dispose() {
		synchronized (this.avatars) {
			for (Avatar avatar : this.avatars.values()) {
				avatar.dispose();
			}
		}
	}

	/**
	 * Is the specified string a valid avatar hash?
	 * 
	 * @param hash
	 * @return true if valid hash, false otherwise
	 */
	public boolean isValidHash(String hash) {
		return hash != null && hash.length() == HASH_LENGTH;
	}

	/**
	 * Get avatar by hash
	 * 
	 * @param hash
	 * @param callback
	 */
	public void loadAvatarByHash(final String hash,
			final IAvatarCallback callback) {
		String title = MessageFormat.format(Messages.AvatarStore_LoadingAvatar,
				hash);
		Job job = new Job(title) {

			protected IStatus run(IProgressMonitor monitor) {
				try {
					Avatar avatar = loadAvatarByHash(hash);
					if (avatar != null && callback != null) {
						callback.loaded(avatar);
					}
				} catch (IOException e) {
					if (callback != null) {
						callback.error(e);
					}
				}
				return Status.OK_STATUS;
			}
		};
		job.setRule(this);
		job.schedule();
	}

	/**
	 * Get avatar by email
	 * 
	 * @param email
	 * @param callback
	 */
	public void loadAvatarByEmail(String email, IAvatarCallback callback) {
		loadAvatarByHash(getHash(email), callback);
	}

	/**
	 * Load latest avatar by specified hash
	 * 
	 * @param hash
	 * @return avatar or null if load fails
	 * @throws IOException
	 */
	public Avatar loadAvatarByHash(String hash) throws IOException {
		if (!isValidHash(hash)) {
			return null;
		}
		Avatar avatar = null;
		URLConnection connection = new URL(this.url + hash).openConnection();
		connection.setConnectTimeout(TIMEOUT);
		ImageLoader loader = new ImageLoader();
		ImageData[] images = loader.load(connection.getInputStream());
		if (images.length > 0) {
			avatar = new Avatar(hash, System.currentTimeMillis(), images[0]);
			this.avatars.put(hash, avatar);
		}
		return avatar;
	}

	/**
	 * Load latest avatar for specified e-mail address
	 * 
	 * @param email
	 * @return avatar or null if load fails
	 * @throws IOException
	 */
	public Avatar loadAvatarByEmail(String email) throws IOException {
		return loadAvatarByHash(getHash(email));
	}

	/**
	 * Get cached avatar by hash
	 * 
	 * @param hash
	 * @return avatar or null if not in cache
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
			if (email.length() > 0) {
				hash = digest(email);
			}
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
		if (element instanceof IAvatarHashProvider) {
			provider = (IAvatarHashProvider) element;
		} else if (element instanceof IAdaptable) {
			provider = (IAvatarHashProvider) ((IAdaptable) element)
					.getAdapter(IAvatarHashProvider.class);
		}
		return provider != null ? provider.getAvatarHash() : element.toString();
	}

	/**
	 * Get cached avatar by e-mail address
	 * 
	 * @param email
	 * @return avatar or null if not in cache
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
