/*******************************************************************************
 *  Copyright (c) 2011 Kevin Sawicki
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package org.github.avatar.ui;

import org.eclipse.osgi.util.NLS;

/**
 * @author Kevin Sawicki (kevin@github.com)
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.github.avatar.ui.messages"; //$NON-NLS-1$

	/**
	 * AvatarPlugin_ExceptionLoadingStore
	 */
	public static String AvatarPlugin_ExceptionLoadingStore;

	/**
	 * AvatarPlugin_ExceptionSavingStore
	 */
	public static String AvatarPlugin_ExceptionSavingStore;

	/**
	 * AvatarPreferencePage_RefreshAvatarsText
	 */
	public static String AvatarPreferencePage_RefreshAvatarsText;

	/**
	 * AvatarStore_LoadingAvatar
	 */
	public static String AvatarStore_LoadingAvatar;

	/**
	 * AvatarStore_RefreshJobName
	 */
	public static String AvatarStore_RefreshJobName;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
