/*******************************************************************************
 *  Copyright (c) 2011 Kevin Sawicki
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package org.github.avatar.ui;

import java.io.Serializable;

import org.eclipse.core.runtime.Assert;

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
	 * Get avatar image as byte array
	 * 
	 * @return non-null byte array
	 */
	public byte[] getBytes() {
		byte[] copy = new byte[this.bytes.length];
		System.arraycopy(this.bytes, 0, copy, 0, copy.length);
		return copy;
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

}
