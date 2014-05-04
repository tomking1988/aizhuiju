package com.tomking.aizhuiju.util;

public class CommonMethods {
	public static String integerToChinese(int i) {
		if(i < 0 || i>=100) {
			return "";
		}
		
		String[] numbers = {
				"零",
				"一",
				"二",
				"三",
				"四",
				"五",
				"六",
				"七",
				"八",
				"九"
		};		
		if(i<10) {
			return numbers[i];
		}
		
		StringBuilder sb = new StringBuilder();
		int firstDigit = i/10;
		
		if(firstDigit > 1) 
			sb.append(numbers[firstDigit]);
		
		sb.append("十");
		if(i%10 > 0)
			sb.append(numbers[i%10]);
		
		return sb.toString();
	}


}
