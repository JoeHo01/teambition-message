package com.d1m.tbmessage.common;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

public class HttpUtil {

	/** The Constant DEFAULT_RESPONSE_TYPE. */
	public static final String DEFAULT_RESPONSE_TYPE = "application/json;charset=UTF-8";

	/** The Constant UTF_8. */
	public static final String UTF_8 = "UTF-8";

	/** The Constant ACCEPT. */
	public static final String ACCEPT = "Accept";

	/** The CONTENT_TYPE */
	public static final String CONTENT_TYPE = "Content-Type";

	/** The teambition header X_API_KEY */
	public static final String X_API_KEY = "x-api-key";

	/** The Constant USER_AGENT */
	public static final String USER_AGENT = "User-Agent";

	/** The Constant AUTHORIZATION */
	public static final String AUTHORIZATION = "Authorization";


	/**
	 * Builds the auth basic.
	 *
	 * @param user the user
	 * @param password the password
	 * @return the string
	 */
	public static String buildAuthBasic(String user, String password) {
		if (StringUtils.isEmpty(user) || StringUtils.isEmpty(password)) {
			return null;
		} else {
			return "Basic " + Base64.encodeBase64String((user + ":" + password).getBytes());
		}
	}
}
