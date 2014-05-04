package com.bilibili.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class SearchResult {
	private String code;
	private HashMap<String, String> property;
	private int page;
	private int pagesize;
	private int total;
	private HashMap<Integer, SearchResultItem> result;
	
	
	public HashMap<Integer, SearchResultItem> getResult() {
		return this.result;
	}
	
	public int getTotal() {
		return this.total;
	}
	
	public int getPage() {
		return this.page;
	}
	
	public int getPageSize() {
		return this.pagesize;
	}
	
	
}
