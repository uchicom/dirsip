/**
 * (c) 2014 uchicom
 */
package com.uchicom.dirsip;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

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
        final SipParameter parameter = new SipParameter(new String[] {"mailbox", "192.168.0.9"});
	final SelectorSipServer server = new SelectorSipServer(parameter);
        if (parameter.init(System.err)) {
            Thread thread = new Thread() {
        	public void run() {
        	    server.execute();
        	}
            };
            thread.setDaemon(true);
            thread.start();
            
        }

	    try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
        for (int i = 0; i < 10000; i++) {
			Thread thread = new Thread() {
				public void run() {
	
					try {
					    Socket socket = new Socket("localhost", 5060);
					    OutputStream os = socket.getOutputStream();
					    os.write(("REGISTER sip:192.168.0.9 SIP/2.0\r\n" +
					    		"Via: SIP/2.0/TCP 192.168.0.8:5060;rport;branch=z9hG4bK1063030321\r\n" +
					    		"From: <sip:456@192.168.0.9>;tag=871567187\r\n" +
					    		"To: <sip:456@192.168.0.9>\r\n" +
					    		"Call-ID: 1483483128\r\n" +
					    		"CSeq: 11 REGISTER\r\n" +
					    		"Contact: <sip:456@192.168.0.8;transport=tcp;line=9ccafc926e3e121>\r\n" +
					    		"Max-Forwards: 70\r\n" + 
					    		"User-Agent: Linphone/3.4.0 (eXosip2/unknown)\r\n" +
					    		"Expires: 0\r\n" +
					    		"Content-Length: 0\r\n\r\n").getBytes());
					    os.flush();
					    Thread.sleep(10000);
					    os.close();
					    socket.close();
				
					} catch (UnknownHostException e) {
					    // TODO Auto-generated catch block
					    e.printStackTrace();
					} catch (IOException e) {
					    // TODO Auto-generated catch block
					    e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			thread.start();
        }

	    try {
			Thread.sleep(100000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
    }

}
