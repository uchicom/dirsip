/**
 * (c) 2014 uchicom
 */
package com.uchicom.dirsip;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * 出力時に一度全てバッファに溜め込むので負荷があがってしまう。
 * 随時書き込むようにしないといけない。
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class SipHandler implements Handler {

    /** 終了フラグ */
    boolean finished;
    
    long startTime = System.currentTimeMillis();
    
    /** ベースディレクトリ */
    File base;


    ByteBuffer readBuff = ByteBuffer.allocate(1024);
    
    ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
    String invite;
    SelectionKey inviteKey;
    LinkedBlockingQueue<StringBuffer> queue = new LinkedBlockingQueue<StringBuffer>();
    
    String uri;
    
    String nonce = Long.toHexString(System.currentTimeMillis());
    String realm;
    String password;

    StringBuilder recieve = new StringBuilder();
    
    private Map<String, SelectionKey> registMap;
    private Map<String, SelectionKey> fromMap;
    public SipHandler(Map<String, SelectionKey> registMap, Map<String, SelectionKey> fromMap, SipParameter parameter) {
    	this.registMap = registMap;
    	this.fromMap = fromMap;
    	this.realm = parameter.getHostName();
    	this.base = parameter.getBase();
    }
    /* (non-Javadoc)
     * @see com.uchicom.dirpop3.Handler#handle(java.nio.channels.SelectionKey)
     */
    @Override
    public void handle(SelectionKey key) throws IOException, NoSuchAlgorithmException {
        SocketChannel channel = (SocketChannel) key.channel();
        if (key.isReadable()) {
            int length = channel.read(readBuff);
            if (length == -1) {
            	if (uri != null) {
            		registMap.put(uri, null);
            		fromMap.remove(uri);
            		System.out.println("ここは？");
            	}
            	channel.close();
            	return;
            } else if (length > 0) {
                check(new String(Arrays.copyOfRange(readBuff.array(), 0, readBuff.position())), key);
                readBuff.clear();
        		if (queue.size() > 0) {
        			key.interestOps(SelectionKey.OP_WRITE | SelectionKey.OP_READ);
        		}
            }

        }
        if (key.isWritable() && queue.size() > 0) {
        	StringBuffer strBuff = queue.poll();
            //初回の出力を実施
            int size = channel.write(ByteBuffer.wrap(strBuff.toString().getBytes()));
        	System.out.println("送信====>");
        	System.out.println(strBuff.toString());
        	System.out.println("<====");
        	if (invite != null) {
        		if (inviteKey.attachment() != null) {
	        		try {
						((SipHandler)inviteKey.attachment()).queue.put(new StringBuffer(invite));
		        		inviteKey.interestOps(SelectionKey.OP_WRITE | SelectionKey.OP_READ);
		        		invite = null;
		        		inviteKey = null;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		}
        	}
            //書き込み処理が終わっていないかを確認する。
            //処理が途中の場合は途中から実施する。
            if (size < strBuff.length()) {
            	strBuff.delete(0, size);
            } else {
            	queue.poll();
	            key.interestOps(SelectionKey.OP_READ);
	            //終了処理
	            if (finished) {
	            	key.cancel();
	            	channel.close();
	            }
            }
        }
    }
    /**
     * コマンド行が入力されたかどうかチェックする.
     * @return
     */
    public void check(String data, SelectionKey key) {

    	System.out.println("受信---->");
    	System.out.println(data);
    	System.out.println("<----");

//		System.out.println("____kaiseki____>");
        kaiseki(data, key);
//		System.out.println("<____kaiseki____");
    }
    public void kaiseki(String data, SelectionKey key) {
    	recieve.append(data);
    	StringBuffer strBuff = null;
    	while (recieve.length() > 0) {
    		String value = recieve.toString();
//    		System.out.println("@@@@@@@@@@@@@@@@@@@");
//    		System.out.println(value);
//    		System.out.println("@@@@@@@@@@@@@@@@@@@");
    		int sepIndex = value.indexOf("\r\n\r\n");
    		if (sepIndex < 0) {
    			//もっと待つ
    			break;
    		}
    		String startLineHeader = value.substring(0, sepIndex + 2);
    		System.out.println("startLineHeader:" + startLineHeader);
    		int contentLengthStartIndex = startLineHeader.indexOf("Content-Length");
    		if (contentLengthStartIndex < 0) {
    			break;
    		}
    		System.out.println("contentLengthStartIndex:" + contentLengthStartIndex);
    		int contentLengthEndIndex = startLineHeader.indexOf("\r\n", contentLengthStartIndex);
    		if (contentLengthEndIndex < 0) {
    			break;
    		}
    		System.out.println("contentLengthEndIndex:" + contentLengthEndIndex);
    		int contentLength = Integer.parseInt(startLineHeader.substring(contentLengthStartIndex, contentLengthEndIndex).split(": *")[1]);
    		System.out.println(contentLength);
    		if (value.length() < sepIndex + contentLength) {
    			//もっと待つ
    			break;
    		}

    		strBuff = new StringBuffer(sepIndex + 4 + contentLength);
	    	String[] lines = startLineHeader.split("\r\n");
	    	String[] methods = lines[0].split(" ");
	    	String method = methods[0];
			int mode = 0;
	    	if ("REGISTER".equals(method)) {
	    		//登録
		        strBuff.append("SIP/2.0 200 OK\r\n");
	            mode = 1;
	    	} else if ("INVITE".equals(method)) {
	    		//呼び出し
	            strBuff.append("SIP/2.0 100 Tring\r\n");
	            mode = 2;
	    	} else if ("CANCEL".equals(method)) {
	    		//呼び出し取り消し
		        strBuff.append("SIP/2.0 200 OK\r\n");
	            mode = 3;
	            //fromMapから消す
	    	} else if ("ACK".equals(method)) {
		        mode = 9;
	    	} else if ("SIP/2.0".equals(method)) {
	    		if ("100".equals(methods[1])) {
	    			//Tring
		    		mode = 4;
	    		} else if ("180".equals(methods[1])) {
	    			//Ringing
	    			mode = 5;
	    		} else if ("200".equals(methods[1])) {
	    			//OK
	    			mode = 6;
	    		} else if ("603".equals(methods[1])) {
	    			//Decline 辞退
	    			//fromMapから消す
	    			mode = 7;
	    		} else if ("486".equals(methods[1])) {
	    			//Busy Here
	    			//fromMapから消す
	    			mode = 8;
	    		}
	    	}
			int bodyLength = -1;
			String fromUser = null;
			boolean authrization = false;
	    	for (int i = 1; i < lines.length; i++) {
	    		String line = lines[i];
	
	            if ("".equals(line)) {
					if (bodyLength <= 0) {
						break;
					} else {
						break;
					}
	            }
				if (line.startsWith("Max-Forwards") ||
						line.startsWith("Contact") ||
						line.startsWith("Content-Type")) {
					//返信に付加しない
				} else if (line.startsWith("Content-Length:")) {
					if (mode == 1 && !authrization) {
						strBuff.replace(8, 14, "401 Unauthorized");
						strBuff.append("WWW-Authenticate: Digest realm=\""+ realm + "\", nonce=\"" + nonce + "\", algorithm=MD5\r\n");
					}
					bodyLength = Integer.parseInt(line.split(" +")[1]);
				    strBuff.append("Content-Length: 0\r\n");
				} else if (line.startsWith("Authorization:")) {
					int usernameIndex = line.indexOf("username=");
					String username = line.substring(usernameIndex + 10, line.indexOf("\"", usernameIndex + 10));
					int uriIndex = line.indexOf("uri=");
					String uriValue = line.substring(uriIndex + 5, line.indexOf("\"", uriIndex + 5));
					int responseIndex = line.indexOf("response=");
					String response = line.substring(responseIndex + 10, line.indexOf("\"", responseIndex + 10));
//					try {
//						MessageDigest md = MessageDigest
//								.getInstance("MD5");
//						if (password == null) {
//							File passwordFile = new File(new File(base, username), "pass.txt");
//							if (passwordFile.exists()
//									&& passwordFile.isFile()) {
//								BufferedReader passReader = null;
//								try {
//									passReader = new BufferedReader(
//												new InputStreamReader(
//														new FileInputStream(
//																passwordFile)));
//									password = passReader.readLine();
//									while ("".equals(password)) {
//										password = passReader.readLine();
//									}
//								} catch (FileNotFoundException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								} catch (IOException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								} finally {
//									if (passReader != null) {
//										try {
//											passReader.close();
//										} catch (IOException e) {
//											// TODO Auto-generated catch block
//											e.printStackTrace();
//										}finally {
//											passReader = null;
//										}
//									}
//								}
//							}
//						}
//						md.update((username + ":" + realm + ":" + password).getBytes());
//						byte[] passBytes = md.digest();
//						StringBuffer buff = new StringBuffer(32);
//						for (int ia = 0; ia < passBytes.length; ia++) {
//							int d = passBytes[ia] & 0xFF;
//							if (d < 0x10) {
//								buff.append("0");
//							}
//							buff.append(Integer.toHexString(d));
//						}
//						String ha = buff.toString();
//						System.out.println("buff=" + buff.toString());
//						md.reset();
//						md.update(("REGISTER:" + uriValue).getBytes());
//						passBytes = md.digest();
//						buff.setLength(0);
//						for (int ia = 0; ia < passBytes.length; ia++) {
//							int d = passBytes[ia] & 0xFF;
//							if (d < 0x10) {
//								buff.append("0");
//							}
//							buff.append(Integer.toHexString(d));
//						}
//						String hb = buff.toString();
//						System.out.println("buff=" + buff.toString());
//						md.update((ha + ":" + nonce + ":" + hb).getBytes());
//						passBytes = md.digest();
//						buff.setLength(0);
//						for (int ia = 0; ia < passBytes.length; ia++) {
//							int d = passBytes[ia] & 0xFF;
//							if (d < 0x10) {
//								buff.append("0");
//							}
//							buff.append(Integer.toHexString(d));
//						}
//						System.out.println(buff.toString());
//						if (response.equals(buff.toString())) {
//							authrization = true;
//						}
//					} catch (NoSuchAlgorithmException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					if (response.equals(createDigest(username, uriValue))) {
						authrization = true;
					}
					System.out.println("reponse=" + response);
				} else {
					strBuff.append(line);
					if (line.startsWith("To")) {
						strBuff.append(";tag=" + System.currentTimeMillis());
						String toUser = line.substring(line.indexOf('<') + 1, line.indexOf('>'));
						if (registMap.containsKey(toUser)) {
							if (mode == 2 || mode == 3 || mode == 9) {
								inviteKey = registMap.get(toUser);
								if (inviteKey == null) {
									//ユーザー接続なし
									int index = strBuff.indexOf("\r\n", 11);
									strBuff.replace(8, index, "408 Request Timeout");
								} else {
									invite = value.substring(0, sepIndex + 4 + contentLength);
//									if (mode == 2) {
//										int insertIndex = invite.indexOf("Via");
//										
//										invite = invite.substring(0, insertIndex) + "Via: SIP/2.0/TCP " + realm + ":5060;rport;branch=z9hG4bK849041310a\r\n"
//												+ invite.substring(insertIndex);
//									}
								}
								System.out.println("INVITE!!" );
							}
						} else {
							File user = new File(base, toUser);
							int index = strBuff.indexOf("\r\n", 11);
							if (user.exists()) {
								strBuff.replace(8, index, "408 Request Timeout");
							} else {
								//ユーザーなし
								strBuff.replace(8, index, "404 Not Found");
							}
						}
					} else if (line.startsWith("From")) {
						fromUser = line.substring(line.indexOf('<') + 1, line.indexOf('>'));
						if (mode == 1) {
							uri = fromUser;
							registMap.put(fromUser, key);
						} else if (mode == 2) {
							uri = fromUser;
							fromMap.put(fromUser, key);
						} else {
							if (fromMap.containsKey(fromUser)) {
								if (mode == 5 || mode == 7 || mode == 8) {
									invite = value.substring(0, sepIndex + 4 + contentLength);
									inviteKey = fromMap.get(fromUser);
									if (inviteKey == null) {
										invite = null;
									}
									System.out.println("RINGING!!" );
								}
							} else {
								//ユーザーなし
								int index = strBuff.indexOf("\r\n", 11);
								strBuff.replace(8, index, "404 Not Found");
							}

							if (mode == 3 || mode == 7 || mode == 8) {
								System.out.println("削除イング");
								fromMap.remove(fromUser);
							}
						}
					} else if (line.startsWith("CSeq")) {
						if (mode == 6 && "INVITE".equals(line.split(" ")[2])) {
							inviteKey = fromMap.get(fromUser);
							if (inviteKey == null) {
								//ユーザーなし
								int index = strBuff.indexOf("\r\n", 11);
								strBuff.replace(8, index, "404 Not Found");
							} else {
								invite = value.substring(0, sepIndex + 4 + contentLength);
								int viaIndex = invite.indexOf("Via");
								invite = invite.substring(0, viaIndex) + invite.substring(invite.indexOf("\r\n", viaIndex) + 2);
							}
							System.out.println("CONNECT!!" );
						}
					}
					strBuff.append("\r\n");
				}
	    	}
			//メッセージボディが無くても空行が必要
	        strBuff.append("\r\n");
			recieve.delete(0, sepIndex + 4 + contentLength);
			try {
				queue.put(strBuff);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
    	}
    }

    /**
     * 認証用のダイジェストを作成する.
     * @param username
     * @param uriValue
     * @return
     */
    private String createDigest(String username, String uriValue) {
    	String digest = null;
		try {
			MessageDigest md = MessageDigest
					.getInstance("MD5");
			if (password == null) {
				loadPassword(username);
			}
			md.update((username + ":" + realm + ":" + password).getBytes());
			String ha = toString(md.digest());
			md.reset();
			md.update(("REGISTER:" + uriValue).getBytes());
			String hb = toString(md.digest());
			md.update((ha + ":" + nonce + ":" + hb).getBytes());
			digest = toString(md.digest());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return digest;
	}
    /**
     * バイト列を文字列表現に変換
     * @param bytes
     * @return
     */
    private String toString(byte[] bytes) {
    	StringBuffer buff = new StringBuffer(bytes.length * 2);
		for (int ia = 0; ia < bytes.length; ia++) {
			int d = bytes[ia] & 0xFF;
			if (d < 0x10) {
				buff.append("0");
			}
			buff.append(Integer.toHexString(d));
		}
		return buff.toString();
    }
   
    /**
     * パスワードを読み込む
     * @param username
     */
    private void loadPassword(String username) {
    	File passwordFile = new File(new File(base, username), "pass.txt");
		if (passwordFile.exists()
				&& passwordFile.isFile()) {
			BufferedReader passReader = null;
			try {
				passReader = new BufferedReader(
							new InputStreamReader(
									new FileInputStream(
											passwordFile)));
				password = passReader.readLine();
				while ("".equals(password)) {
					password = passReader.readLine();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (passReader != null) {
					try {
						passReader.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally {
						passReader = null;
					}
				}
			}
		}
    }
}
