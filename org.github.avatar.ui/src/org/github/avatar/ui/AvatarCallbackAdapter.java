/*******************************************************************************
 *  Copyright (c) 2011 Kevin Sawicki
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package org.github.avatar.ui;

/**
 * Base implementation of {@link IAvatarCallback}
 * 
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
