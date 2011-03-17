package org.github.avatar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class AvatarPlugin extends AbstractUIPlugin {

	/**
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "org.github.avatar"; //$NON-NLS-1$

	/**
	 * STORE_NAME
	 */
	public static final String STORE_NAME = "avatars.store"; //$NON-NLS-1$

	// The shared instance
	private static AvatarPlugin plugin;

	private AvatarStore store;

	/**
	 * The constructor
	 */
	public AvatarPlugin() {
	}

	/**
	 * Get avatar store
	 * 
	 * @return avatars
	 */
	public AvatarStore getAvatars() {
		return this.store;
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		IPath location = Platform.getStateLocation(context.getBundle());
		File store = location.append(STORE_NAME).toFile();
		if (store != null && store.exists()) {
			try {
				ObjectInputStream stream = new ObjectInputStream(
						new FileInputStream(store));
				this.store = (AvatarStore) stream.readObject();
			} catch (IOException e) {
				getLog().log(
						new Status(IStatus.ERROR, PLUGIN_ID,
								"Exception loading avatar store", e)); //$NON-NLS-1$
			} catch (ClassNotFoundException cnfe) {
				getLog().log(
						new Status(IStatus.ERROR, PLUGIN_ID,
								"Exception loading avatar store", cnfe)); //$NON-NLS-1$
			}
		}
		if (this.store == null) {
			this.store = new AvatarStore();
		}
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 *      )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		IPath location = Platform.getStateLocation(context.getBundle());
		File store = location.append(STORE_NAME).toFile();
		if (store != null) {
			try {
				ObjectOutputStream stream = new ObjectOutputStream(
						new FileOutputStream(store));
				stream.writeObject(this.store);
			} catch (IOException e) {
				getLog().log(
						new Status(IStatus.ERROR, PLUGIN_ID,
								"Exception saving avatar store", e)); //$NON-NLS-1$
			}
		}
		this.store.dispose();
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static AvatarPlugin getDefault() {
		return plugin;
	}

}
