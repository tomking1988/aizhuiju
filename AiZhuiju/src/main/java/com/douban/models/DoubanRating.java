package com.douban.models;

public class DoubanRating {

	private int min;
	private int max;
	private double average;
	private int numRaters;
	/*
	public int getAverage() {
		return average;
	}*/
	
	public void setMin(int min){
		this.min = min;
	}
	
	public int getMin() {
		return this.min;
	}
	
	public void setMax(int max) {
		this.max = max;
	}
	
	public int getMax() {
		return max;
	}
	
	public double getAverage() {
		return average;
	}
	
	public int getNumRaters() {
		return numRaters;
	}

    public void setAverage(double average) {
        this.average = average;
    }
}
