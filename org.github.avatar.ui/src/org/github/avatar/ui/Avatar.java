/*******************************************************************************
 *  Copyright (c) 2011 Kevin Sawicki
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package org.github.avatar.ui;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;

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
 * Avatar class containing id and image data.
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public class Avatar implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 7303486086217698261L;

	private String id;
	private long updateTime;
	private byte[] bytes;
	private transient ImageData data;

	/**
	 * Create avatar
	 * 
	 * @param id
	 * @param updateTime
	 * @param bytes
	 */
	public Avatar(String id, long updateTime, byte[] bytes) {
		Assert.isNotNull(id, "Id cannot be null"); //$NON-NLS-1$
		Assert.isNotNull(bytes, "Bytes cannot be null"); //$NON-NLS-1$
		this.id = id;
		this.updateTime = updateTime;
		this.bytes = bytes;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return this.id.hashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		else if (obj instanceof Avatar)
			return getId().equals(((Avatar) obj).getId());
		return false;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.id;
	}

	/**
	 * Get avatar id
	 * 
	 * @return id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Get time avatar was loaded
	 * 
	 * @return update time
	 */
	public long getUpdateTime() {
		return this.updateTime;
	}

	/**
	 * Get avatar image data
	 * 
	 * @return image data
	 */
	public ImageData getData() {
		if (this.data == null) {
			ByteArrayInputStream stream = new ByteArrayInputStream(this.bytes);
			try {
				ImageData[] images = new ImageLoader().load(stream);
				if (images.length > 0)
					this.data = images[0];
				else
					this.data = ImageDescriptor.getMissingImageDescriptor()
							.getImageData();
			} catch (SWTException exception) {
				this.data = ImageDescriptor.getMissingImageDescriptor()
						.getImageData();
			} finally {
				try {
					stream.close();
				} catch (IOException ignore) {
				}
			}
		}
		return this.data;
	}

	/**
	 * Get avatar image scaled to specified size. The returned image should be
	 * managed and properly disposed by the caller.
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
