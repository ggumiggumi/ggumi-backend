package com.uplus.ggumi.dto.token;

import com.uplus.ggumi.domain.parent.Role;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
@Builder
public class TokenInfoDto {

	private final String grantType;
	private final String accessToken;
	private final String refreshToken;
	private final Role role;

}
