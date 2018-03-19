package com.d1m.tbmessage.common.annotation;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class AnnotationUtil {

	/**
	 * If there are fields annotated by @Name in the Class, return Map(key: annotation name, value: field name).
	 * Ignore fields without annotated.
	 * Return empty Map if there are no fields annotated.
	 *
	 * @param clazz the entity
	 * @return value
	 */
	public static Map<String, String> getNamedField(Class clazz) {
		Map<String, String> result = new HashMap<>();
		for (Field field : clazz.getDeclaredFields()) {
			if (field.getAnnotation(Name.class) == null) continue;
			String key = field.getAnnotation(Name.class).value();
			if ("".equals(key)) key = field.getName();
			result.put(key, field.getName());
		}
		return result;
	}
}
