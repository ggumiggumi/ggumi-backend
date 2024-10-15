package com.uplus.ggumi.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uplus.ggumi.config.response.ResponseDto;
import com.uplus.ggumi.config.response.ResponseUtil;
import com.uplus.ggumi.dto.token.IdTokenDto;
import com.uplus.ggumi.dto.token.RefreshTokenDto;
import com.uplus.ggumi.dto.token.TokenInfoDto;
import com.uplus.ggumi.service.OAuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/oauth")
public class OAuthController {

	private final OAuthService oAuthService;

	@PostMapping("/kakao")
	public ResponseDto<TokenInfoDto> kakaoLogin(@RequestBody IdTokenDto idTokenDto) {
		return ResponseUtil.SUCCESS("카카오 로그인에 성공하였습니다.", oAuthService.kakaoOAuthLogin(idTokenDto.getIdToken()));
	}

	@PostMapping("/refresh-token")
	public ResponseDto<TokenInfoDto> reGenerateAccessToken(@RequestBody RefreshTokenDto refreshTokenDto) {
		return ResponseUtil.SUCCESS("토큰을 재발급하였습니다.", oAuthService.reGenerateAccessToken(refreshTokenDto));
	}

}
