/**
 * 
 */
package org.github.avatar;

/**
 * @author Kevin Sawicki (kevin@github.com)
 */
public abstract class AvatarCallbackAdapter implements IAvatarCallback {

	/**
	 * @see org.github.avatar.IAvatarCallback#loaded(org.github.avatar.Avatar)
	 */
	public void loaded(Avatar avatar) {
		// Does nothing sub-clsases should override
	}

	/**
	 * @see org.github.avatar.IAvatarCallback#error(java.lang.Exception)
	 */
	public void error(Exception exception) {
		// Does nothing sub-clsases should override
	}

}
