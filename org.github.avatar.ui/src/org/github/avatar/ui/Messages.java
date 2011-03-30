/**
 * 
 */
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
