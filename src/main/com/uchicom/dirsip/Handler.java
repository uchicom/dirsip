/**
 * (c) 2014 uchicom
 */
package com.uchicom.dirsip;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.security.NoSuchAlgorithmException;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public interface Handler {

    public void handle(SelectionKey key) throws IOException, NoSuchAlgorithmException;
}
