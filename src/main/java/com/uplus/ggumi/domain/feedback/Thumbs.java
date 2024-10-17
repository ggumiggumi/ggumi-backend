package com.uplus.ggumi.domain.feedback;

public enum Thumbs {
	UP("좋아요"),
	DOWN("싫어요"),
	UNCHECKED("무상태")
	;

	private String description;

	Thumbs(String description) {
		this.description = description;
	}
}
