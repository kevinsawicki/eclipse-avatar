/*******************************************************************************
 *  Copyright (c) 2011 Kevin Sawicki
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package org.github.avatar.ui;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Avatar store interface.
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public interface IAvatarStore {

	/**
	 * Does this store contain the specified avatar for the hash
	 * 
	 * @param hash
	 * @return true if store contains avatar, false otherwise
	 */
	boolean containsAvatar(String hash);

	/**
	 * Refresh all avatars currently in the store
	 * 
	 * @param monitor
	 * @return this store
	 */
	IAvatarStore refresh(IProgressMonitor monitor);

	/**
	 * Schedule refresh of avatars asynchronously
	 * 
	 * @return this store
	 */
	IAvatarStore scheduleRefresh();

	/**
	 * Get last refresh time of store
	 * 
	 * @return local refresh time
	 */
	long getRefreshTime();

	/**
	 * Load avatar by hash asynchronously
	 * 
	 * @param hash
	 * @param callback
	 * @return this store
	 */
	IAvatarStore loadAvatarByHash(String hash, IAvatarCallback callback);

	/**
	 * Load latest avatar by specified hash
	 * 
	 * @param hash
	 * @return avatar or null if load fails
	 * @throws IOException
	 */
	Avatar loadAvatarByHash(String hash) throws IOException;

	/**
	 * Load avatar by e-mail address asynchronously
	 * 
	 * @param email
	 * @param callback
	 * @return this store
	 */
	IAvatarStore loadAvatarByEmail(String email, IAvatarCallback callback);

	/**
	 * Load latest avatar for specified e-mail address
	 * 
	 * @param email
	 * @return avatar or null if load fails
	 * @throws IOException
	 */
	Avatar loadAvatarByEmail(String email) throws IOException;

	/**
	 * Get cached avatar by hash
	 * 
	 * @param hash
	 * @return avatar or null if not in cache
	 */
	Avatar getAvatarByHash(String hash);

	/**
	 * Get cached avatar by e-mail address
	 * 
	 * @param email
	 * @return avatar or null if not in cache
	 */
	Avatar getAvatarByEmail(String email);

}
