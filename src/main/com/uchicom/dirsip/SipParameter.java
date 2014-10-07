/**
 * (c) 2014 uchicom
 */
package com.uchicom.dirsip;

import java.io.File;
import java.io.PrintStream;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class SipParameter {
    private File base;
    private String hostName;
    private int port;
    private int back;
    private int pool;
    
    private String[] args;
    public SipParameter(String[] args) {
        this.args = args;
    }
  
    /**
     * 初期化
     * @param ps
     * @return
     */
    public boolean init(PrintStream ps) {

        if (args.length < 1) {
            ps.println("args.length < 1");
            return false;
        }
        // ユーザディレクトリ格納フォルダ
        base = SipStatic.DEFAULT_MAILBOX;

        if (args.length > 1) {
            base = new File(args[0]);
        }
        if (!base.exists() || !base.isDirectory()) {
            System.err.println("user directory is not found.");
            return false;
        }

        // ホスト名
        hostName = args[1];

        // ポート
        port = SipStatic.DEFAULT_PORT;
        if (args.length > 2) {
            port = Integer.parseInt(args[2]);
        } 
        // 接続待ち数
        back =  SipStatic.DEFAULT_BACK;
        if (args.length > 3) {
            back = Integer.parseInt(args[3]);
        }
        return true;
    }
    
    public String getHostName() {
        return hostName;
    }
    
    public int getPort() {
        return port;
    }
    public int getBack() {
        return back;
    }
    public File getBase() {
        return base;
    }
    public int getPool() {
        return pool;
    }
}
