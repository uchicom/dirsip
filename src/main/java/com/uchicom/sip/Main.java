// (c) 2016 uchicom
package com.uchicom.sip;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class Main {

	public static void main(String[] args) {
		SipParameter parameter = new SipParameter(args);
		if (parameter.init(System.err)) {
			parameter.createServer().execute();
		}
	}

}
