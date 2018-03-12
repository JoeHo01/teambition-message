package com.d1m.tbmessage.common.annotation;

import java.lang.reflect.Field;

public class AnnotationUtil {

	/**
	 * get value in annotation @Name
	 *
	 * @param field field
	 * @return value
	 */
	public static String getName(Field field) {
		if (field.getAnnotation(Name.class) == null) return null;
		String value = field.getAnnotation(Name.class).value();
		if ("".equals(value)) {
			value = field.getName();
		}
		return value;
	}
}
