/*******************************************************************************
 *  Copyright (c) 2011 Kevin Sawicki
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package org.github.avatar.ui;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * Class to wrap an {@link IAvatarCallback} instance and run it as a runnable on
 * the UI-thread.
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public class AvatarDisplayCallback extends AvatarCallbackAdapter {

	private IAvatarCallback wrapped;
	private boolean async = true;

	/**
	 * Create avatar display callback
	 * 
	 * @param callback
	 */
	public AvatarDisplayCallback(IAvatarCallback callback) {
		Assert.isNotNull(callback, "Callback cannot be null"); //$NON-NLS-1$
		this.wrapped = callback;
	}

	/**
	 * Set async execution of wrapped callback
	 * 
	 * @param async
	 * @return this callback
	 */
	public AvatarDisplayCallback setAsync(boolean async) {
		this.async = async;
		return this;
	}

	private void displayExec(Runnable runnable) {
		Display display = PlatformUI.getWorkbench().getDisplay();
		if (this.async)
			display.asyncExec(runnable);
		else
			display.syncExec(runnable);
	}

	/**
	 * @see org.github.avatar.ui.AvatarCallbackAdapter#loaded(org.github.avatar.ui.Avatar)
	 */
	public void loaded(final Avatar avatar) {
		Runnable runnable = new Runnable() {

			public void run() {
				wrapped.loaded(avatar);
			}
		};
		displayExec(runnable);
	}

	/**
	 * @see org.github.avatar.ui.AvatarCallbackAdapter#error(java.lang.Exception)
	 */
	public void error(final Exception exception) {
		Runnable runnable = new Runnable() {

			public void run() {
				wrapped.error(exception);
			}
		};
		displayExec(runnable);
	}

}
