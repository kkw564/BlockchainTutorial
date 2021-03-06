package com.kau.bitcointest.node;
/**
 * Developer Jaewook Lim (Korea Aerospace Univ.)
 * 
 * 
 */
import java.util.HashMap;

public interface NodeInterface {
	public String serverStartOption(String basedir);
	public String cliOption(String basedir);
	public String getClientId();
	public void setClientId(String client_id);
}
