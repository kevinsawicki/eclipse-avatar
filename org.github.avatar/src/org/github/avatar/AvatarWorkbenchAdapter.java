/**
 * 
 */
package org.github.avatar;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.WorkbenchAdapter;

/**
 * @author Kevin Sawicki (kevin@github.com)
 */
public class AvatarWorkbenchAdapter extends WorkbenchAdapter {

	private String getHash(Object element) {
		IAvatarHashProvider provider = null;
		if (element instanceof IAvatarHashProvider) {
			provider = (IAvatarHashProvider) element;
		} else if (element instanceof IAdaptable) {
			provider = (IAvatarHashProvider) ((IAdaptable) element)
					.getAdapter(IAvatarHashProvider.class);
		}
		return provider != null ? provider.getAvatarHash() : element.toString();
	}

	/**
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getImageDescriptor(java.lang.Object)
	 */
	public ImageDescriptor getImageDescriptor(Object object) {
		String hash = getHash(object);
		Avatar avatar = AvatarPlugin.getDefault().getAvatars()
				.getAvatarByHash(hash);
		return avatar != null ? ImageDescriptor.createFromImageData(avatar
				.getData()) : super.getImageDescriptor(object);
	}
}
