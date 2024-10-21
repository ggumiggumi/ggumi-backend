package com.uplus.ggumi.dto.token;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefreshTokenDto {
	@Schema(description = "리프레시 토큰(Bearer 필요)")
	private String refreshToken;
}
