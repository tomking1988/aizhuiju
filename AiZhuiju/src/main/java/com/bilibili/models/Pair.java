package com.bilibili.models;

public class Pair implements Comparable<Pair>{

	private String key;
	private String val;
	
	public Pair(String key, String val) {
		this.key = key;
		this.val = val;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public String getVal() {
		return this.val;
	}
	
	@Override
	public int compareTo(Pair other) {
		// TODO Auto-generated method stub
		return this.key.compareTo(other.key);
		 
	}
}
