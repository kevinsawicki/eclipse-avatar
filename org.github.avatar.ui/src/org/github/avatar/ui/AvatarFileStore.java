/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.github.avatar.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * @author Kevin Sawicki (kevin@github.com)
 */
public class AvatarFileStore {

	/**
	 * STORE_NAME
	 */
	public static final String DEFAULT_STORE_NAME = "avatars.ser"; //$NON-NLS-1$

	private File file;

	/**
	 * Create avatar file store
	 * 
	 * @param file
	 */
	public AvatarFileStore(File file) {
		this.file = file;
	}

	/**
	 * Create avatar file store in bundle state location with default name
	 * 
	 * @param bundle
	 */
	public AvatarFileStore(Bundle bundle) {
		this(Platform.getStateLocation(bundle).append(DEFAULT_STORE_NAME)
				.toFile());
	}

	/**
	 * Load avatars
	 * 
	 * @return store
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public AvatarStore load() throws IOException, ClassNotFoundException {
		if (!file.exists())
			return null;

		ObjectInputStream stream = null;
		try {
			stream = new ObjectInputStream(new FileInputStream(file));
			return (AvatarStore) stream.readObject();
		} finally {
			if (stream != null)
				try {
					stream.close();
				} catch (IOException ignore) {
				}
		}
	}

	/**
	 * Save avatars
	 * 
	 * @param avatars
	 * @return this file store
	 * @throws IOException
	 */
	public AvatarFileStore save(AvatarStore avatars) throws IOException {
		ObjectOutputStream stream = null;
		try {
			stream = new ObjectOutputStream(new FileOutputStream(file));
			stream.writeObject(avatars);
		} finally {
			if (stream != null)
				try {
					stream.close();
				} catch (IOException ignore) {
				}
		}
		return this;
	}

}
