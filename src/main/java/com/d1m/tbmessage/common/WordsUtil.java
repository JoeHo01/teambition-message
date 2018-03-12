package com.d1m.tbmessage.common;

public class WordsUtil {
	public static String upperFirstCase(String words){
		String firstCase = words.substring(0, 1).toUpperCase();
		return firstCase + words.substring(1);
	}
}
