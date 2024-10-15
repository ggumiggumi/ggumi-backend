package com.uplus.ggumi.dto.token;

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

}
