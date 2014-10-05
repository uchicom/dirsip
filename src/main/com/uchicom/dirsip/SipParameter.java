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

        // ポート
        port = SipStatic.DEFAULT_PORT;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
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
