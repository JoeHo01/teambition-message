package com.d1m.tbmessage.server.database.util;

import net.sf.json.JSONObject;

public class SqlUtil {
	/**
	 *
	 * @param object the DB mapping entity
	 * @param column columns in sequence
	 * @return insert words
	 */
	public static String insertValue(Object object, String[] column) {
		StringBuilder value = new StringBuilder();
		value.append('(');
		JSONObject jsonObject = JSONObject.fromObject(object);
		for (int i = 0; i < column.length; i++) {
			if (i > 0) value.append(',');
			value.append('\'');
			value.append(jsonObject.getString(column[i]));
			value.append('\'');
		}
		value.append(')');
		return value.toString();
	}
}
