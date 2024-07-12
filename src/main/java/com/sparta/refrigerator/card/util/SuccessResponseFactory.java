package com.sparta.refrigerator.card.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class SuccessResponseFactory {

	private final static String MSG_OK = "The request has been successfully processed.";

	public static <T> ResponseEntity<SuccessResponse<T>> ok(T data) {
		SuccessResponse<T> response = new SuccessResponse<>(data, MSG_OK, HttpStatus.OK.value());
		return ResponseEntity.ok(response);
	}

	public static ResponseEntity<SuccessResponse<Void>> ok() {
		SuccessResponse<Void> response = new SuccessResponse<>(null, MSG_OK, HttpStatus.OK.value());
		return ResponseEntity.ok(response);
	}

	public static ResponseEntity<Void> noContent() {
		return ResponseEntity.noContent().build();
	}
}
