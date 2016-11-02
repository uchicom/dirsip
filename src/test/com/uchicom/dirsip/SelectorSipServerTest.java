/**
 * (c) 2014 uchicom
 */
package com.uchicom.dirsip;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelectionKey;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class SelectorSipServerTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void registTest() {
        final SipParameter parameter = new SipParameter(new String[] {"-dir", "mailbox", "-host", "192.168.0.9"});
        SipHandler handler = new SipHandler(parameter);
        try {
			handler.handle(new AbstractSelectionKey() {

				@Override
				public SelectableChannel channel() {
					return new SocketChannel(null) {

						@Override
						public <T> T getOption(SocketOption<T> name) throws IOException {
							// TODO 自動生成されたメソッド・スタブ
							return null;
						}

						@Override
						public Set<SocketOption<?>> supportedOptions() {
							// TODO 自動生成されたメソッド・スタブ
							return null;
						}

						@Override
						public SocketChannel bind(SocketAddress local) throws IOException {
							// TODO 自動生成されたメソッド・スタブ
							return null;
						}

						@Override
						public boolean connect(SocketAddress remote) throws IOException {
							// TODO 自動生成されたメソッド・スタブ
							return false;
						}

						@Override
						public boolean finishConnect() throws IOException {
							// TODO 自動生成されたメソッド・スタブ
							return false;
						}

						@Override
						public SocketAddress getLocalAddress() throws IOException {
							// TODO 自動生成されたメソッド・スタブ
							return null;
						}

						@Override
						public SocketAddress getRemoteAddress() throws IOException {
							// TODO 自動生成されたメソッド・スタブ
							return null;
						}

						@Override
						public boolean isConnected() {
							// TODO 自動生成されたメソッド・スタブ
							return false;
						}

						@Override
						public boolean isConnectionPending() {
							// TODO 自動生成されたメソッド・スタブ
							return false;
						}

						@Override
						public int read(ByteBuffer dst) throws IOException {
							byte[] bytes = ("REGISTER sip:192.168.0.9 SIP/2.0\r\n" +
						    		"Via: SIP/2.0/TCP 192.168.0.8:5060;rport;branch=z9hG4bK1063030321\r\n" +
						    		"From: <sip:456@192.168.0.9>;tag=871567187\r\n" +
						    		"To: <sip:456@192.168.0.9>\r\n" +
						    		"Call-ID: 1483483128\r\n" +
						    		"CSeq: 11 REGISTER\r\n" +
						    		"Contact: <sip:456@192.168.0.8;transport=tcp;line=9ccafc926e3e121>\r\n" +
						    		"Max-Forwards: 70\r\n" +
						    		"User-Agent: Linphone/3.4.0 (eXosip2/unknown)\r\n" +
						    		"Expires: 0\r\n" +
						    		"Content-Length: 0\r\n\r\n").getBytes();
							dst.put(bytes);
							return bytes.length;
						}

						@Override
						public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
							// TODO 自動生成されたメソッド・スタブ
							return 0;
						}

						@Override
						public <T> SocketChannel setOption(SocketOption<T> name, T value) throws IOException {
							// TODO 自動生成されたメソッド・スタブ
							return null;
						}

						@Override
						public SocketChannel shutdownInput() throws IOException {
							// TODO 自動生成されたメソッド・スタブ
							return null;
						}

						@Override
						public SocketChannel shutdownOutput() throws IOException {
							// TODO 自動生成されたメソッド・スタブ
							return null;
						}

						@Override
						public Socket socket() {
							// TODO 自動生成されたメソッド・スタブ
							return null;
						}

						@Override
						public int write(ByteBuffer src) throws IOException {
							// TODO 自動生成されたメソッド・スタブ
							return 0;
						}

						@Override
						public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
							// TODO 自動生成されたメソッド・スタブ
							return 0;
						}

						@Override
						protected void implCloseSelectableChannel() throws IOException {
							// TODO 自動生成されたメソッド・スタブ

						}

						@Override
						protected void implConfigureBlocking(boolean block) throws IOException {
							// TODO 自動生成されたメソッド・スタブ

						}


					};
				}

				@Override
				public int interestOps() {
					// TODO 自動生成されたメソッド・スタブ
					return 0;
				}

				@Override
				public SelectionKey interestOps(int ops) {
					// TODO 自動生成されたメソッド・スタブ
					return null;
				}

				@Override
				public int readyOps() {
					return OP_READ;
				}

				@Override
				public Selector selector() {
					// TODO 自動生成されたメソッド・スタブ
					return null;
				}

			});
			assertEquals(1, Context.singleton().getRegistMap().size());
			assertEquals(0, Context.singleton().getFromMap().size());
		} catch (IOException e) {
			fail();
			e.printStackTrace();
		}

    }

}
