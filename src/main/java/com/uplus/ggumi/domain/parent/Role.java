package com.uplus.ggumi.domain.parent;

public enum Role {

	USER("유저"),
	ADMIN("관리자")
	;

	private String description;

	Role(String description) {
		this.description = description;
	}

}
