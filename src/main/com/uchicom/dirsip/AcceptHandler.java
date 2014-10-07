/**
 * (c) 2014 uchicom
 */
package com.uchicom.dirsip;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Map;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class AcceptHandler implements Handler {

	/**
	 * 登録情報.
	 */
	private Map<String ,SelectionKey> registMap;
	/**
	 * Invite情報.
	 */
	private Map<String ,SelectionKey> fromMap;
	private SipParameter parameter;
	public AcceptHandler(Map<String, SelectionKey> registMap, Map<String, SelectionKey> fromMap, SipParameter parameter) {
		this.registMap = registMap;
		this.fromMap = fromMap;
		this.parameter = parameter;
	}
    /* (non-Javadoc)
     * @see com.uchicom.http.Handler#handle(java.nio.channels.SelectionKey)
     */
    @Override
    public void handle(SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            //サーバーの受付処理。
            SocketChannel socketChannel = ((ServerSocketChannel)key.channel()).accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(key.selector(), SelectionKey.OP_READ, new SipHandler(registMap, fromMap, parameter));
        }
    }

}
