// (c) 2016 uchicom
package com.uchicom.dirsip;

import java.nio.channels.SelectionKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class Context {

	private static Context context = new Context();

    private Map<String, SelectionKey> registMap = new ConcurrentHashMap<>();
    private Map<String, SelectionKey> fromMap = new ConcurrentHashMap<>();

	private Context() {

	}
	public static Context singleton() {
		return context;
	}

	/**
	 * registMapを取得します.
	 *
	 * @return registMap
	 */
	public Map<String, SelectionKey> getRegistMap() {
		return registMap;
	}
	/**
	 * fromMapを取得します.
	 *
	 * @return fromMap
	 */
	public Map<String, SelectionKey> getFromMap() {
		return fromMap;
	}
}
