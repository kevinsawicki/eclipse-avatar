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
 * Interface for providing an avatar hash.
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public interface IAvatarHashProvider {

	/**
	 * Get hash for avatar lookup
	 * 
	 * @return avatar hash
	 */
	String getAvatarHash();

}
