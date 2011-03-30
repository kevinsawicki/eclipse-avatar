package org.github.avatar.ui;

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
 * Avatar plug-in that contains a persistent {@link AvatarStore} instance
 * available for use.
 */
public class AvatarPlugin extends AbstractUIPlugin {

	/**
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "org.github.avatar.ui"; //$NON-NLS-1$

	/**
	 * STORE_NAME
	 */
	public static final String STORE_NAME = "avatars.ser"; //$NON-NLS-1$

	/**
	 * Create error status
	 * 
	 * @param message
	 * @param throwable
	 * @return status
	 */
	public static IStatus createErrorStatus(String message, Throwable throwable) {
		return new Status(IStatus.ERROR, PLUGIN_ID, message, throwable);
	}

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
			ObjectInputStream stream = null;
			try {
				stream = new ObjectInputStream(new FileInputStream(store));
				this.store = (AvatarStore) stream.readObject();
			} catch (IOException e) {
				log(Messages.AvatarPlugin_ExceptionLoadingStore, e);
			} catch (ClassNotFoundException cnfe) {
				log(Messages.AvatarPlugin_ExceptionLoadingStore, cnfe);
			} finally {
				if (stream != null) {
					try {
						stream.close();
					} catch (IOException ignore) {
					}
				}
			}
		}
		if (this.store == null) {
			this.store = new AvatarStore();
		}
	}

	private void log(String message, Throwable throwable) {
		IStatus status = createErrorStatus(message, throwable);
		getLog().log(status);
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
			ObjectOutputStream stream = null;
			try {
				stream = new ObjectOutputStream(new FileOutputStream(store));
				stream.writeObject(this.store);
			} catch (IOException e) {
				log(Messages.AvatarPlugin_ExceptionSavingStore, e);
			} finally {
				if (stream != null) {
					try {
						stream.close();
					} catch (IOException ignore) {
					}
				}
			}
		}
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
