/**
 * (c) 2014 uchicom
 */
package com.uchicom.dirsip;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class AcceptHandler implements Handler {

	private Map<String ,SelectionKey> registMap = new HashMap<String, SelectionKey>();
    /* (non-Javadoc)
     * @see com.uchicom.http.Handler#handle(java.nio.channels.SelectionKey)
     */
    @Override
    public void handle(SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            //サーバーの受付処理。
            SocketChannel socketChannel = ((ServerSocketChannel)key.channel()).accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(key.selector(), SelectionKey.OP_READ, new SipHandler(registMap));
        }
    }

}
