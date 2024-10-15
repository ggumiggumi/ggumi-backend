package com.uplus.ggumi.config.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ErrorResponse {

	private String message;
	private int code;
	private int status;

	@Builder
	public ErrorResponse(String message, int code, int status) {
		this.message = message;
		this.code = code;
		this.status = status;
	}

	public static ErrorResponse of(ErrorCode errorCode) {
		return ErrorResponse.builder()
			.message(errorCode.getMessage())
			.code(errorCode.getCode())
			.status(errorCode.getStatus())
			.build();
	}

}
