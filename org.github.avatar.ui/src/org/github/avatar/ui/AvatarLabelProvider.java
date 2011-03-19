/**
 * 
 */
package org.github.avatar.ui;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

/**
 * @author Kevin Sawicki (kevin@github.com)
 */
public class AvatarLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	/**
	 * DEFAULT_IMAGE_SIZE
	 */
	public static final int DEFAULT_IMAGE_SIZE = 32;

	private AvatarStore store = AvatarPlugin.getDefault().getAvatars();

	private ColumnViewer viewer;
	private int imageSize = DEFAULT_IMAGE_SIZE;

	/**
	 * Create avatar label provider for viewer
	 * 
	 * @param viewer
	 */
	public AvatarLabelProvider(ColumnViewer viewer) {
		this.viewer = viewer;
	}

	/**
	 * Set image size
	 * 
	 * @param size
	 * @return this label provider
	 */
	public AvatarLabelProvider setImageSize(int size) {
		this.imageSize = Math.max(1, size);
		return this;
	}

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(final Object element) {
		Image scaled = null;
		String hash = this.store.getHash(element.toString());
		Avatar avatar = this.store.getAvatarByHash(hash);
		if (avatar != null) {
			scaled = avatar.getScaledImage(this.imageSize);
		} else {
			store.loadAvatarByHash(hash, new AvatarCallbackAdapter() {

				public void loaded(Avatar avatar) {
					PlatformUI.getWorkbench().getDisplay()
							.asyncExec(new Runnable() {

								public void run() {
									if (!viewer.getControl().isDisposed()) {
										viewer.refresh(element);
									}
								}
							});
				}

			});
		}
		return scaled;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object,
	 *      int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			return getImage(element);
		}
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object,
	 *      int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		if (columnIndex == 0) {
			return getText(element);
		}
		return ""; //$NON-NLS-1$
	}

}
