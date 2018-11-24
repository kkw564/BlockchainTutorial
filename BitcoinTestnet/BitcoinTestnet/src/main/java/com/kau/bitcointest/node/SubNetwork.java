package com.kau.bitcointest.node;

import java.util.HashMap;

import com.google.gson.Gson;

public class SubNetwork implements NodeInterface{

	private Integer port;
	private Integer rpcport;
	private String id;
	private Integer connect_port;
	private String client_id;
	
	public SubNetwork(String id, Integer port, Integer rpcport, Integer connect_port) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.port = port;
		this.rpcport = rpcport;
		this.connect_port = connect_port;
	}
	
	public String serverStartOption(String basedir) {
		return "-connect=127.0.0.1:"+connect_port
				+ " -port="+port
				+ " -rpcport="+rpcport
				+ " -datadir="+basedir+id
				+ " -daemon";
	}
	@Override
	public String cliOption(String basedir) {
		// TODO Auto-generated method stub
		return "-datadir="+basedir+id
				+ " -rpcport="+rpcport;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		HashMap<String, String> map = new HashMap<>();
		map.put("id", id);
		map.put("rpcport", String.valueOf(rpcport));

		
		return gson.toJson(map, map.getClass());
	}
	@Override
	public String getClientId() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setClientId(String client_id) {
		// TODO Auto-generated method stub
		
	}
}
