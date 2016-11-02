/**
 * (c) 2014 uchicom
 */
package com.uchicom.sip;

import java.io.PrintStream;

import com.uchicom.server.Parameter;
import com.uchicom.server.SelectorServer;
import com.uchicom.server.Server;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class SipParameter extends Parameter {

    public SipParameter(String[] args) {
        super(args);
    }

    /**
     * 初期化
     * @param ps
     * @return
     */
    public boolean init(PrintStream ps) {

    	// メールボックスの基準フォルダ
    	if (!is("dir")) {
    		put("dir", Constants.DEFAULT_DIR);
    	}
        // 実行するサーバのタイプ
    	if (!is("type")) {
    		put("type", "single");
    	}
    	// ホスト名
    	if (!is("host")) {
    		put("host", "localhost");
    	}
    	// 待ち受けポート
    	if (!is("port")) {
    		put("port", Constants.DEFAULT_PORT);
    	}
    	// 受信する接続 (接続要求) のキューの最大長
    	if (!is("back")) {
    		put("back", Constants.DEFAULT_BACK);
    	}
    	// プールするスレッド数
    	if (!is("pool")) {
    		put("pool", Constants.DEFAULT_POOL);
    	}
		return true;
	}



    public Server createServer() {
    	Server server = null;
		switch (get("type")) {
		case "selector":
			server = new SelectorServer(this, ()->{
				return  new SipHandler(this);
			});
			break;
		}

    	return server;
    }

}
