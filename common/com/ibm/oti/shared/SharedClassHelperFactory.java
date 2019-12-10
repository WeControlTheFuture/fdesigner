package com.ibm.oti.shared;

public interface SharedClassHelperFactory {
	public SharedClassURLHelper getURLHelper(ClassLoader classLoader) throws HelperAlreadyDefinedException;

}
