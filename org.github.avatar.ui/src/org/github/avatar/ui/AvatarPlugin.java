/*******************************************************************************
 *  Copyright (c) 2011 Kevin Sawicki
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package org.github.avatar.ui;

import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

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
	private ServiceRegistration storeRegistration;

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

		try {
			this.store = new AvatarFileStore(context.getBundle()).load();
		} catch (IOException e) {
			log(Messages.AvatarPlugin_ExceptionLoadingStore, e);
		} catch (ClassNotFoundException cnfe) {
			log(Messages.AvatarPlugin_ExceptionLoadingStore, cnfe);
		}
		if (this.store == null)
			this.store = new AvatarStore();

		this.storeRegistration = context.registerService(
				IAvatarStore.class.getName(), this.store, null);
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

		if (this.storeRegistration != null) {
			this.storeRegistration.unregister();
			this.storeRegistration = null;
		}

		try {
			new AvatarFileStore(context.getBundle()).save(store);
		} catch (IOException e) {
			log(Messages.AvatarPlugin_ExceptionSavingStore, e);
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
