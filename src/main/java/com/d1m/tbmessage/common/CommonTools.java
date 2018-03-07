package com.d1m.tbmessage.common;

import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.alibaba.fastjson.JSONObject;
import com.vdurmont.emoji.EmojiParser;

/**
 * 常用工具类
 * 
 * @author Joe He
 * @date 创建时间：2017年4月8日 下午10:59:55
 * @version 1.0
 *
 */
public class CommonTools {

	/**
	 * 正则表达式处理工具
	 * 
	 * @author Joe He
	 * @date 2017年4月9日 上午12:27:10
	 * @return
	 */
	public static Matcher getMatcher(String regEx, String text) {
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(text);
		return matcher;
	}

	/**
	 * xml解析器
	 * 
	 * @author Joe He
	 * @date 2017年4月9日 下午6:24:25
	 * @param text
	 * @return
	 */
	public static Document xmlParser(String text) {
		Document doc = null;
		StringReader sr = new StringReader(text);
		InputSource is = new InputSource(sr);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * 处理emoji表情
	 * 
	 * @author Joe He
	 * @date 2017年4月23日 下午2:39:04
	 * @param d
	 * @param k
	 */
	public static void emojiFormatter(JSONObject d, String k) {
		Matcher matcher = getMatcher("<span class=\"emoji emoji(.{1,10})\"></span>", d.getString(k));
		StringBuilder sb = new StringBuilder();
		String content = d.getString(k);
		int lastStart = 0;
		while (matcher.find()) {
			String str = matcher.group(1);
			if (str.length() == 6) {

			} else if (str.length() == 10) {

			} else {
				str = "&#x" + str + ";";
				String tmp = content.substring(lastStart, matcher.start());
				sb.append(tmp + str);
				lastStart = matcher.end();
			}
		}
		if (lastStart < content.length()) {
			sb.append(content.substring(lastStart));
		}
		if (sb.length() != 0) {
			d.put(k, EmojiParser.parseToUnicode(sb.toString()));
		} else {
			d.put(k, content);
		}

	}

	/**
	 * 消息格式化
	 * 
	 * @author Joe He
	 * @date 2017年4月23日 下午4:19:08
	 * @param d
	 * @param k
	 */
	public static void msgFormatter(JSONObject d, String k) {
		d.put(k, d.getString(k).replace("<br/>", "\n"));
		emojiFormatter(d, k);
		// TODO 与emoji表情有部分兼容问题，目前暂未处理解码处理 d.put(k,
		// StringEscapeUtils.unescapeHtml4(d.getString(k)));
	}
}
