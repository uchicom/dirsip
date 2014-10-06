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
        final SipParameter parameter = new SipParameter(new String[0]);
	final SelectorSipServer server = new SelectorSipServer(parameter);
        if (parameter.init(System.err)) {
            Thread thread = new Thread() {
        	public void run() {
        	    server.execute();
        	}
            };
            thread.setDaemon(true);
        }
	
	try {
	    Socket socket = new Socket("localhost", 5060);
	    OutputStream os = socket.getOutputStream();
	    os.write("REGISTER ".getBytes());
	} catch (UnknownHostException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	
    }

}
