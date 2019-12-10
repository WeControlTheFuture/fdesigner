package com.ibm.oti.shared;

import java.net.URL;

public interface SharedClassURLHelper {

	public byte[] findSharedClass(String partition, URL sourceFileURL, String name);
	
	public boolean storeSharedClass(String partition, URL sourceFileURL, Class clazz);

	public boolean setMinimizeUpdateChecks();
}
