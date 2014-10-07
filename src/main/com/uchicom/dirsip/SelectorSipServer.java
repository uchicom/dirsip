/**
 * (c) 2014 uchicom
 */
package com.uchicom.dirsip;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Channelを利用したSIPサーバー
 * 
 * @author uchicom: Shigeki Uchiyama
 * 
 */
public class SelectorSipServer {

	protected static Queue<ServerSocketChannel> serverQueue = new ConcurrentLinkedQueue<ServerSocketChannel>();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SipParameter parameter = new SipParameter(args);
		if (parameter.init(System.err)) {
			SelectorSipServer server = new SelectorSipServer(parameter);
			server.execute();
		}
	}

	private static boolean alive = true;
	private SipParameter parameter;
	private Map<String ,SelectionKey> registMap = new HashMap<String, SelectionKey>();
	private Map<String ,SelectionKey> fromMap = new HashMap<String, SelectionKey>();

	public SelectorSipServer(SipParameter parameter) {
		this.parameter = parameter;
	}

	/**
	 * メイン処理
	 * 
	 */
	public void execute() {
		ServerSocketChannel server = null;
		try {
			server = ServerSocketChannel.open();
			server.socket().setReuseAddress(true);
			server.socket().bind(new InetSocketAddress(parameter.getPort()),
					parameter.getBack());
			server.configureBlocking(false);
			serverQueue.add(server);

			final Selector selector = Selector.open();
			server.register(selector, SelectionKey.OP_ACCEPT,
					new AcceptHandler(registMap, fromMap, parameter));
			Thread thread = new Thread() {
				public void run() {
					try {
						while (true) {
							System.out.println(" keys:" +
									+ selector.keys().size() +
									",regist:" +
									+ registMap.size() +
									",from:"
									+ fromMap.size());
							Thread.sleep(2000);
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			thread.setDaemon(true);
			thread.start();
			while (alive) {
				if (selector.select() > 0) {
					Set<SelectionKey> keys = selector.selectedKeys();
					Iterator<SelectionKey> ite = keys.iterator();
					while (ite.hasNext()) {
						SelectionKey key = ite.next();
						ite.remove();
						try {
							if (key.isValid()) {
								((Handler) key.attachment()).handle(key);
							} else {
								System.out.println("key.cancel!");
								key.cancel();
							}
						} catch (IOException e) {
							e.printStackTrace();
							key.cancel();
						} catch (NoSuchAlgorithmException e) {
							e.printStackTrace();
							key.cancel();
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			synchronized (server) {
				if (server != null) {
					try {
						server.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					server = null;
				}
			}
		}
	}

	public static void shutdown(String[] args) {
		if (!serverQueue.isEmpty()) {
			try {
				serverQueue.poll().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
