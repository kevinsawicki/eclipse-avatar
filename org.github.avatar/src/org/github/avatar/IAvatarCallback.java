/**
 * 
 */
package org.github.avatar;

/**
 * @author Kevin Sawicki (kevin@github.com)
 */
public interface IAvatarCallback {

	/**
	 * Avatar loaded successfully
	 * 
	 * @param avatar
	 */
	void loaded(Avatar avatar);

	/**
	 * Avatar loading failed
	 * 
	 * @param exception
	 */
	void error(Exception exception);

}
