package com.d1m.tbmessage.common.util;

import org.apache.commons.lang.StringUtils;

public class WordsUtil {
	public static String upperFirstCase(String words){
		if (StringUtils.isEmpty(words)) return words;
		String firstCase = words.substring(0, 1).toUpperCase();
		return firstCase + words.substring(1);
	}
}
