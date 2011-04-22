/*******************************************************************************
 *  Copyright (c) 2011 Kevin Sawicki
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package org.github.avatar.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.WorkbenchAdapter;

/**
 * Avatar workbench adapter for providing an image descriptor for object that
 * can adapt to an avatar hash.
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public class AvatarWorkbenchAdapter extends WorkbenchAdapter {

	/**
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getImageDescriptor(java.lang.Object)
	 */
	public ImageDescriptor getImageDescriptor(Object object) {
		AvatarStore store = AvatarPlugin.getDefault().getAvatars();
		String hash = store.getAdaptedHash(object);
		Avatar avatar = store.getAvatarByHash(hash);
		return avatar != null ? ImageDescriptor
				.createFromImageData(new AvatarImage(avatar).getData()) : super
				.getImageDescriptor(object);
	}
}
