/**
 * 
 */
package org.github.avatar;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.ui.PlatformUI;

/**
 * @author Kevin Sawicki (kevin@github.com)
 */
public class Avatar implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 7303486086217698261L;

	private String id;
	private long updateTime;
	private transient ImageData data;
	private transient Image image;

	/**
	 * Create avatar
	 * 
	 * @param id
	 * @param updateTime
	 * @param data
	 */
	public Avatar(String id, long updateTime, ImageData data) {
		Assert.isNotNull(id, "Id cannot be null"); //$NON-NLS-1$
		Assert.isNotNull(data, "Image data cannot be null"); //$NON-NLS-1$
		this.id = id;
		this.updateTime = updateTime;
		this.data = data;
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
		if (obj == this) {
			return true;
		} else if (obj instanceof Avatar) {
			Avatar avatar = (Avatar) obj;
			return getId().equals(avatar.getId());
		}
		return false;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.id;
	}

	private void readObject(ObjectInputStream stream) throws IOException,
			ClassNotFoundException {
		stream.defaultReadObject();

		ImageLoader loader = new ImageLoader();
		ImageData[] images = loader.load(stream);
		if (images.length == 0) {
			throw new IOException(
					"No image data loaded from object input stream"); //$NON-NLS-1$
		}
		this.data = images[0];
	}

	private void writeObject(ObjectOutputStream stream) throws IOException {
		stream.defaultWriteObject();

		ImageLoader loader = new ImageLoader();
		loader.data = new ImageData[] { this.data };
		loader.save(stream, SWT.IMAGE_JPEG);
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
		return this.data;
	}

	/**
	 * Get avatar image.
	 * 
	 * @return image
	 */
	public Image getImage() {
		if (this.image == null || this.image.isDisposed()) {
			this.image = new Image(PlatformUI.getWorkbench().getDisplay(),
					this.data);
		}
		return this.image;
	}

	/**
	 * Dispose current image
	 */
	public void dispose() {
		if (this.image != null) {
			this.image.dispose();
		}
	}

	/**
	 * Get avatar image scaled to specified size. The returned image should be
	 * managed and properly disposed by the caller.
	 * 
	 * @param size
	 * @return scaled image
	 */
	public Image getScaledImage(int size) {
		Image image = getImage();
		Image scaled = new Image(PlatformUI.getWorkbench().getDisplay(), size,
				size);
		GC gc = new GC(scaled);
		try {
			gc.setAntialias(SWT.ON);
			gc.setInterpolation(SWT.HIGH);
			Rectangle sourceBounds = image.getBounds();
			Rectangle targetBounds = scaled.getBounds();
			gc.drawImage(image, 0, 0, sourceBounds.width, sourceBounds.height,
					0, 0, targetBounds.width, targetBounds.height);
		} finally {
			gc.dispose();
		}
		return scaled;
	}
}
