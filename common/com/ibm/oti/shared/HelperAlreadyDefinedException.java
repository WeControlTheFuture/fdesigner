package com.ibm.oti.shared;

public class HelperAlreadyDefinedException extends Exception {

	private static final long serialVersionUID = -356665797623954170L;

	public HelperAlreadyDefinedException (String s) {
		super(s);
	}
}
