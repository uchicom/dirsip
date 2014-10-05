/**
 * (c) 2014 uchicom
 */
package com.uchicom.dirsip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Uchiyama Shigeki
 *
 */
public class SingleSipServer {

	public static ServerSocket server;
	/**
	 * アドレスとメールユーザーフォルダの格納フォルダを指定する
	 * @param args
	 */
	public static void main (String[] args) {
		execute();
	}
	public SingleSipServer() {
	}
	private static void execute() {
        try {
            server = new ServerSocket();
            server.setReuseAddress(true);
            server.bind(new InetSocketAddress(5060), 10);
            SingleSipServer sipServer = new SingleSipServer();
            while (true) {
                Socket socket = server.accept();
                sipServer.sip(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    server = null;
                }
            }
        }
	}
	static Map<String, String> map = new HashMap<String, String>();
	public void sip(Socket socket) {
	    String address = String.valueOf( socket.getRemoteSocketAddress());
		System.out.println(System.currentTimeMillis() + ":" + address);
		try {
			BufferedReader isr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintStream ps = new PrintStream(socket.getOutputStream());
			StringBuffer strBuff = new StringBuffer();
			System.out.println("read");
			String line = isr.readLine();
			int bodyLength = -1;
			char[] body = null;
			int mode = 0;
			String user = null;
			String url = null;
			while (line != null) {
			while (line != null) {
                if ("".equals(line)) {
    				if (bodyLength <= 0) {
    					break;
    				} else {
    					body = new char[bodyLength];
    					isr.read(body);
    					break;
    				}
                }
			    System.out.println(line);
				if (line.startsWith("REGISTER")) {
		            strBuff.append("SIP/2.0 200 OK\r\n");
		            mode = 1;
//		            ps.print("SIP/2.0 200 OK\r\n");
//		            //接続に対する受付開始
//		            ps.flush();
				} else if (line.startsWith("INVITE")) {
		            mode =2;
				    //プロキシーの場合
//				    strBuff.append("SIP/2.0 100 Trying\r\n");
				    //直接の場合
                    strBuff.append("SIP/2.0 100 Ringing\r\n");
//                    ps.print("SIP/2.0 180 Ringing\r\n");
//                    ps.flush();
				} else if (line.startsWith("Max-Forwards") ||
						line.startsWith("Contact") ||
						line.startsWith("Content-Type")) {
					//返信に付加しない
				} else if (line.startsWith("Content-Length:")) {
					bodyLength = Integer.parseInt(line.split(" +")[1]);
				    strBuff.append("Content-Length: 0\r\n");
				} else {
					strBuff.append(line);
					if (line.startsWith("To")) {
						strBuff.append(";tag=" + System.currentTimeMillis());
					} else if (mode == 1) {
						if (line.startsWith("From")) {
							user = line.substring(line.indexOf('<') + 1, line.indexOf('>'));
						} else if (line.startsWith("Via")) {
							url = line.split("[ ;]")[2];
						}
					}
					strBuff.append("\r\n");
				}
				line = isr.readLine();
			}
			map.put(user, url);
			//メッセージボディが無くても空行が必要
            strBuff.append("\r\n");
            System.out.println("--------->");
            System.out.println(strBuff.toString());
            System.out.println("<---------");
			ps.print(strBuff.toString());
			strBuff.setLength(0);
			mode=0;
			bodyLength = -1;
			//接続に対する受付開始
			ps.flush();
			line = isr.readLine();
			}
			ps.close();
			isr.close();
			System.out.println(map);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					socket = null;
				}
			}
			
		}
	}

	public static void shutdown(String[] args) {
		synchronized (server) {
			if (server != null) {
				try {
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
