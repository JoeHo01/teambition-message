package com.d1m.tbmessage.common.exception;

public enum ErrorCode {

	HTTP_RESPONSE_STATUS_ERROR(1100, "Error status from HTTP response"),
	HTTP_RESPONSE_PAYLOAD_ERROR(1101, "Error payload from HTTP response")

	;

	private int code;
	private String message;


	ErrorCode(int i, String message) {

	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
