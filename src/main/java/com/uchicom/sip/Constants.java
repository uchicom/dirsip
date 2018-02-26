package com.uchicom.sip;

public class Constants {
	// SMTP返却メッセージ
	/** 返却メッセージ(250(成功応答)) */
	public static String RECV_250 = "250";
	/** 返却メッセージ(250 OK(成功応答)) */
	public static String RECV_250_OK = "250 OK\r\n";
	/** 返却メッセージ(550(失敗応答)) */
	public static String RECV_550 = "550 ";
	/** 返却メッセージ(354(中間応答)) */
	public static String RECV_354 = "354 Enter Mail end With\",\"\r\n";

	// No such user here

	// SMTPコマンド正規表現
	/** EHELOの正規表現(大文字小文字後続スペース) */
	public static String REG_EXP_EHELO = "^[Ee][Hh][Ee][Ll][Oo] *$";
	/** HELOの正規表現(大文字小文字後続スペース) */
	public static String REG_EXP_HELO = "^[Hh][Ee][Ll][Oo] *$";
	/** MAIL FROM:アドレスの正規表現(大文字小文字後続スペース) */
	public static String REG_EXP_MAIL_FROM = "^[Mm][Aa][Ii][Ll] [Ff][Rr][Oo][Mm]:.* *$";
	/** RCPT TO:アドレスの正規表現(大文字小文字後続スペース) */
	public static String REG_EXP_RCPT_TO = "^[Rr][Cc][Pp][Tt] [Tt][Oo]:.* *$";
	/** DATAの正規表現(大文字小文字後続スペース) */
	public static String REG_EXP_DATA = "^[Dd][Aa][Tt][Aa] *$";
	/** QUITの正規表現(大文字小文字後続スペース) */
	public static String REG_EXP_QUIT = "^[Qq][Uu][Ii][Tt] *$";

	//POP3 STARTTLS拡張コマンド
	/** STLSの正規表現(大文字小文字後続スペース) */
    public static String REG_EXP_STLS = "^[Ss][Tt][Ll][Ss] *$";

	/** 日時書式 */
	public static String DATE_TIME_MILI_FORMAT = "yyyyMMdd_HHmmss.SSS";

	/** パスワードファイルのパス */
	public static String PASSWORD_FILE_NAME = ".pass";

	//初期設定
	/** デフォルトメールボックスディレクトリ */
    public static String DEFAULT_DIR = "mailbox";
    /** デフォルト待ち受けポート番号 */
    public static String DEFAULT_PORT = "5060";
    /** デフォルト接続待ち数 */
	public static String DEFAULT_BACK = "10";
	/** デフォルトスレッドプール数 */
	public static String DEFAULT_POOL = "10";
}
