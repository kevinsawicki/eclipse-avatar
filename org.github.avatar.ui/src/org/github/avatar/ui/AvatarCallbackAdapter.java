/**
 * 
 */
package org.github.avatar.ui;

/**
 * @author Kevin Sawicki (kevin@github.com)
 */
public abstract class AvatarCallbackAdapter implements IAvatarCallback {

	/**
	 * @see org.github.avatar.ui.IAvatarCallback#loaded(org.github.avatar.ui.Avatar)
	 */
	public void loaded(Avatar avatar) {
		// Does nothing sub-clsases should override
	}

	/**
	 * @see org.github.avatar.ui.IAvatarCallback#error(java.lang.Exception)
	 */
	public void error(Exception exception) {
		// Does nothing sub-clsases should override
	}

}
