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
	 * AvatarStore_LoadingAvatar
	 */
	public static String AvatarStore_LoadingAvatar;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
