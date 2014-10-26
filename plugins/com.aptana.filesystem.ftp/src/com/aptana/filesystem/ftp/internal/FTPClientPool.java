/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.filesystem.ftp.internal;

import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.core.runtime.Platform;

import com.aptana.core.util.KeepAliveObjectPool;
import com.aptana.filesystem.ftp.FTPPlugin;
import com.aptana.filesystem.ftp.preferences.FTPPreferenceInitializer;
import com.aptana.filesystem.ftp.preferences.IFTPPreferenceConstants;
import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPClientInterface;
import com.enterprisedt.net.ftp.FTPTransferType;

public final class FTPClientPool extends KeepAliveObjectPool<FTPClientInterface> {

	// N3X: Keep connection pools so we don't have to re-init every fucking time.
	protected static Map<String,FTPClientPool> pools = new WeakHashMap<String,FTPClientPool>();

	private IPoolConnectionManager manager;

	public FTPClientPool(IPoolConnectionManager manager) {
		super(Platform.getPreferencesService().getInt(FTPPlugin.PLUGIN_ID, IFTPPreferenceConstants.KEEP_ALIVE_TIME,
				FTPPreferenceInitializer.DEFAULT_KEEP_ALIVE_MINUTES, null) * 60 * 1000);
		this.manager = manager;
		start();
	}

	public FTPClientInterface create() {
		return manager.newClient();
	}

	public void expire(FTPClientInterface ftpClient) {
		if (ftpClient == null) {
			return;
		}
		try {
			ftpClient.quit();
		} catch (Exception e) {
			try {
				ftpClient.quitImmediately();
			} catch (Exception ignore) {
				ignore.getCause();
			}
		}
	}

	public boolean validate(FTPClientInterface o) {
		if (!o.connected()) {
			return false;
		}
		if (o instanceof FTPClient) {
			try {
				((FTPClient) o).noOperation();
				((FTPClient) o).setType(FTPTransferType.BINARY);
			} catch (Exception e) {
				// ignore
				return false;
			}
		}
		return true;
	}
	
	public static String buildPoolKey(BaseFTPConnectionFileManager fileMan) {
		StringBuilder sb = new StringBuilder();
		sb.append(fileMan.getLogin());
		sb.append("@");
		sb.append(fileMan.host);
		sb.append(":");
		sb.append(fileMan.port);
		return sb.toString();
	}

	/**
	 * Less connection spam.
	 * 
	 * @param fileMan Manager checking out this pool
	 * @author Rob "N3X15" Nelson <nexisentertainment@gmail.com>
	 * @return
	 */
	public static FTPClientPool checkoutPool(BaseFTPConnectionFileManager fileMan)
	{
		String poolKey = FTPClientPool.buildPoolKey(fileMan);
		if(!pools.containsKey(poolKey)) {
			pools.put(poolKey,new FTPClientPool((IPoolConnectionManager) fileMan));
		}
		return pools.get(poolKey);
	}
}
