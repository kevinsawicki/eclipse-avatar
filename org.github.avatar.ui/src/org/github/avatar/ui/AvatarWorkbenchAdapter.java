/**
 * 
 */
package org.github.avatar.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.WorkbenchAdapter;

/**
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
		return avatar != null ? ImageDescriptor.createFromImageData(avatar
				.getData()) : super.getImageDescriptor(object);
	}
}
