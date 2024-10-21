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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "소셜로그인, JWT토큰")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/oauth")
public class OAuthController {

	private final OAuthService oAuthService;

	@Operation(summary = "카카오 소셜로그인")
	@PostMapping("/kakao")
	public ResponseDto<TokenInfoDto> kakaoLogin(@RequestBody IdTokenDto idTokenDto) {
		return ResponseUtil.SUCCESS("카카오 로그인에 성공하였습니다.", oAuthService.kakaoOAuthLogin(idTokenDto.getIdToken()));
	}

	@Operation(summary = "토큰 재발급", description = "리프레시 토큰 앞에 토큰 타입 'Bearer ' 필요")
	@PostMapping("/refresh-token")
	public ResponseDto<TokenInfoDto> reGenerateAccessToken(@RequestBody RefreshTokenDto refreshTokenDto) {
		return ResponseUtil.SUCCESS("토큰을 재발급하였습니다.", oAuthService.reGenerateAccessToken(refreshTokenDto));
	}

}
