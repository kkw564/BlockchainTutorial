package com.kau.bitcointest;
/**
 * Developer Jaewook Lim (Korea Aerospace Univ.)
 * 
 * 
 */
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.kau.bitcointest.database.BitcoinNodeDatabase;
import com.kau.bitcointest.node.Network;
import com.kau.bitcointest.node.NodeInterface;
import com.kau.bitcointest.node.SubNetwork;
import com.kau.bitcointest.terminal.CommandUtils;

@RestController
public class BitcoinTestnetConfigController {
	private final String datadir = "/Users/jaewook/BitcoinUser/";
	private final Integer rpcport_base = 3000;
	private final Integer port_base = 4000;
	private static ArrayList<NodeInterface> nodes = BitcoinNodeDatabase.getInstance().getNodeArray();
	private Network networkNode;
	@RequestMapping("regtest/initsession")
	public void initSession() throws IOException {
		
		if(nodes != null) {
			for (int i = 0; i < nodes.size(); i++) {
				NodeInterface tmpNode = nodes.get(i);
				stopClient(tmpNode);
			}
			nodes.clear();
		}
//		nodes = new ArrayList<>();
		if(networkNode != null) {
			stopClient(networkNode);
		}
		
		File file = new File(datadir); 
		FileUtils.deleteDirectory(file);
		FileUtils.forceMkdir(file);
		FileUtils.forceMkdir(new File(datadir+"network"));
		Network network = new Network("network", "rpc", "rpc", 8335, 8336);
		CommandUtils.execute("bitcoind -regtest "+network.serverStartOption(datadir));
//		CommandUtils.execute("bitcoin-cli -regtest "+network.cliOption(datadir)+" generate 101");
		networkNode = network;
	}
	
	@RequestMapping("regtest/createsession")
	public String createSession(@RequestParam("num") Integer num) throws IOException {
		ArrayList<String> jsonarray = new ArrayList<>();
		Gson gson = new Gson();
		for (int i = 0; i < num; i++) {
			SubNetwork node = new SubNetwork(String.valueOf(i), port_base+i, rpcport_base+i, networkNode.getPort());
			nodes.add(node);
			jsonarray.add(node.toString());
			FileUtils.forceMkdir(new File(datadir+String.valueOf(i)));
			CommandUtils.execute("bitcoind -regtest "+node.serverStartOption(datadir));
		}
		return gson.toJson(jsonarray);
	}
	
	@RequestMapping("regtest/addsession")
	public String addSession(@RequestParam("num") Integer num) throws IOException {
		ArrayList<String> jsonarray = new ArrayList<>();
		ArrayList<NodeInterface> node_array = BitcoinNodeDatabase.getInstance().getNodeArray();
		int start = node_array.size();
		for (int i = 0; i < num; i++) {
			SubNetwork node = new SubNetwork(String.valueOf(i+start), port_base+i+start, rpcport_base+i+start, networkNode.getPort());
			node_array.add(node);
			jsonarray.add(node.toString());
			FileUtils.forceMkdir(new File(datadir+String.valueOf(i+start)));
			CommandUtils.execute("bitcoind -regtest "+node.serverStartOption(datadir));
		}
		
		Gson gson = new Gson();
		return gson.toJson(jsonarray);
	}
	public void stopClient(NodeInterface node) {
		CommandUtils.execute("bitcoin-cli -regtest "+node.cliOption(datadir) + " stop");
	}
	
	@RequestMapping("regtest/generateblock")
	public String generateBlock(@RequestParam("block_num")Integer block_num) {
		return CommandUtils.execute("bitcoin-cli -regtest "+ networkNode.cliOption(datadir)+ " generate "+block_num);
	}
	
	@RequestMapping("regtest/networktoaddress")
	public String networkToAddress(@RequestParam("to_address")String to_address, @RequestParam("btc") Float btc) {
		System.out.println("to_address="+to_address+"/btc="+btc);
		return CommandUtils.execute("bitcoin-cli -regtest "+ networkNode.cliOption(datadir)+ " sendtoaddress \""+to_address + "\" " + btc);
	}
	
	@RequestMapping("regtest/loadnode")
	public String loadNodeInfo(@RequestParam("client_id") String client_id, @RequestParam("node_id")String node_id) {
		ArrayList<NodeInterface> node_array = BitcoinNodeDatabase.getInstance().getNodeArray();
		Integer index = Integer.parseInt(node_id);
		System.out.println("[DEBUG] Bitcoin size"+BitcoinNodeDatabase.getInstance().getNodeArray().size());
		NodeInterface node = node_array.get(index);
		node.setClientId(client_id);
		node_array.set(index, node);
		SubNetwork subnet = (SubNetwork)node;
		Gson gson = new Gson();
		return gson.toJson(subnet.getNodeInfo());
	}
	
	@RequestMapping("regtest/autoloadnode")
	public String autoLoadNodeInfo(@RequestParam("client_id") String client_id) {
		ArrayList<NodeInterface> node_array = BitcoinNodeDatabase.getInstance().getNodeArray();
		for (int i = 0; i < node_array.size(); i++) {
			NodeInterface node = node_array.get(i);
			if(client_id.equals(node.getClientId())) {
				SubNetwork subnet = (SubNetwork)node;
				Gson gson = new Gson();
				return gson.toJson(subnet.getNodeInfo());
			}
		}
		synchronized (node_array) {
			for (int i = 0; i < node_array.size(); i++) {
				NodeInterface node = node_array.get(i);
				String tmp_client_id = node.getClientId();
				if(tmp_client_id == null) {
					node.setClientId(client_id);
					SubNetwork subnet = (SubNetwork)node;
					Gson gson = new Gson();
					return gson.toJson(subnet.getNodeInfo());
				}
			}
		}
		return null;
	}
	@RequestMapping("regtest/getallnodeinfo")
	public String getAllNodeInfo() {
		ArrayList<HashMap<String, String>> result_array = new ArrayList<>();
		ArrayList<NodeInterface> node_array = BitcoinNodeDatabase.getInstance().getNodeArray();
		for (int i = 0; i < node_array.size(); i++) {
			SubNetwork node = (SubNetwork)node_array.get(i);
			result_array.add(node.getNodeInfo());
		}
		Gson gson = new Gson();
		
		return gson.toJson(result_array);
	}
}