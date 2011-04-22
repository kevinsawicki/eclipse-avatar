/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.github.avatar.ui;

import java.io.ByteArrayInputStream;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * Avatar image class.
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public class AvatarImage {

	private Avatar avatar;
	private ImageData data;

	/**
	 * Create avatar image from avatar
	 * 
	 * @param avatar
	 */
	public AvatarImage(Avatar avatar) {
		Assert.isNotNull(avatar, "Avatar cannot be null"); //$NON-NLS-1$
		this.avatar = avatar;
	}

	/**
	 * Get avatar image data
	 * 
	 * @return image data
	 */
	public ImageData getData() {
		if (this.data != null)
			return this.data;

		try {
			ImageData[] images = new ImageLoader()
					.load(new ByteArrayInputStream(avatar.getBytes()));
			if (images.length > 0)
				this.data = images[0];
			else
				this.data = ImageDescriptor.getMissingImageDescriptor()
						.getImageData();
		} catch (SWTException exception) {
			this.data = ImageDescriptor.getMissingImageDescriptor()
					.getImageData();
		}
		return this.data;
	}

	/**
	 * Get avatar image scaled to specified size. The returned image should be
	 * managed and properly disposed of by the caller.
	 * 
	 * @param size
	 * @return scaled image
	 */
	public Image getScaledImage(int size) {
		Display display = PlatformUI.getWorkbench().getDisplay();
		Image image = new Image(display, getData());
		Rectangle sourceBounds = image.getBounds();

		// Return original image and don't scale if size matches request
		if (sourceBounds.width == size)
			return image;

		Image scaled = new Image(display, size, size);
		GC gc = new GC(scaled);
		try {
			gc.setAntialias(SWT.ON);
			gc.setInterpolation(SWT.HIGH);
			Rectangle targetBounds = scaled.getBounds();
			gc.drawImage(image, 0, 0, sourceBounds.width, sourceBounds.height,
					0, 0, targetBounds.width, targetBounds.height);
		} finally {
			gc.dispose();
			image.dispose();
		}
		return scaled;
	}

}
