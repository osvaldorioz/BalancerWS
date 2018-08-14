package mx.org.ift.balancer.variables;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

import mx.org.ift.balancer.vo.MessageVO;

public final class Vars {
	private final static Vars vars = new Vars();
	private final TreeMap<String, MessageVO> mp = new TreeMap<String, MessageVO>();
	private final List<MessageVO> buffer = new ArrayList<MessageVO>();
	private String version;
	private Integer peak;
	
	private Vars(){
		version = "ver. " + new Random().nextDouble();
	}
	
	private final TreeMap<Integer, String> servers = new TreeMap<Integer, String>();
	private Integer principal;
	private Integer top;
	private Integer counter;
	private Boolean status;
	
	public static Vars getInstance(){
		return vars;
	}

	/**
	 * @return the servers
	 */
	public TreeMap<Integer, String> getServers() {
		return servers;
	}

	/*
	 * @param servers the servers to set
	 
	public void setServers(TreeMap<Integer, String> servers) {
		this.servers = servers;
	}*/

	/**
	 * @return the principal
	 */
	public Integer getPrincipal() {
		return principal;
	}

	/**
	 * @param principal the principal to set
	 */
	public void setPrincipal(Integer principal) {
		this.principal = principal;
	}

	/**
	 * @return the top
	 */
	public Integer getTop() {
		return top;
	}

	/**
	 * @param top the top to set
	 */
	public void setTop(Integer top) {
		this.top = top;
	}

	/**
	 * @return the counter
	 */
	public Integer getCounter() {
		return counter;
	}

	/**
	 * @param counter the counter to set
	 */
	public void setCounter(Integer counter) {
		this.counter = counter;
	}

	/**
	 * @return the status
	 */
	public Boolean getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Boolean status) {
		this.status = status;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the mp
	 */
	public TreeMap<String, MessageVO> getMp() {
		return mp;
	}

	/**
	 * @return the buffer
	 */
	public List<MessageVO> getBuffer() {
		return buffer;
	}

	/**
	 * @return the peak
	 */
	public Integer getPeak() {
		return peak;
	}

	/**
	 * @param peak the peak to set
	 */
	public void setPeak(Integer peak) {
		this.peak = peak;
	}

	
	
}
