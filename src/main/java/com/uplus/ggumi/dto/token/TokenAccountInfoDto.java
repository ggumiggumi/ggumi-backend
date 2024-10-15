package com.uplus.ggumi.dto.token;

import com.uplus.ggumi.domain.parent.Parent;
import com.uplus.ggumi.domain.parent.Provider;
import com.uplus.ggumi.domain.parent.Role;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TokenAccountInfoDto {

	private Long id;
	private String email;
	private Provider provider;
	private String nickname;
	private Role role;

	@Builder
	public TokenAccountInfoDto(Parent parent) {
		this.id = parent.getId();
		this.email = parent.getEmail();
		this.provider = parent.getProvider();
		this.nickname = parent.getNickname();
		this.role = parent.getRole();
	}

	@Getter
	@Builder
	public static class TokenInfo {
		private String email;
		private String provider;
	}


}
