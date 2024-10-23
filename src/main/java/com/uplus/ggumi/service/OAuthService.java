package com.uplus.ggumi.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.uplus.ggumi.config.exception.ApiException;
import com.uplus.ggumi.config.exception.ErrorCode;
import com.uplus.ggumi.config.jwt.JwtTokenProvider;
import com.uplus.ggumi.domain.parent.Parent;
import com.uplus.ggumi.domain.parent.Provider;
import com.uplus.ggumi.domain.parent.Role;
import com.uplus.ggumi.dto.token.RefreshTokenDto;
import com.uplus.ggumi.dto.token.TokenAccountInfoDto;
import com.uplus.ggumi.dto.token.TokenInfoDto;
import com.uplus.ggumi.repository.ParentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuthService {

	private final KakaoSocialService kakaoSocialService;
	private final ParentRepository parentRepository;
	private final JwtTokenProvider jwtTokenProvider;

	public TokenInfoDto kakaoOAuthLogin(String kakaoAccessToken) {
		HashMap<String, Object> kakaoUserInfo = kakaoSocialService.getKakaoUserInfo(kakaoAccessToken);
		String email = kakaoUserInfo.get("email").toString();
		Provider provider = Provider.KAKAO;

		if (parentRepository.notExistsAccountByEmailAndProvider(email, provider)) {
			saveAccount(kakaoUserInfo, email);
		}
		return jwtTokenProvider.generateToken(getAuthentication(email, String.valueOf(provider)));
	}

	private void saveAccount(HashMap<String, Object> kakaoUserInfo, String email) {
		String nickname = kakaoUserInfo.get("nickname").toString();
		Parent parent = Parent.builder()
			.provider(Provider.KAKAO)
			.email(email)
			.nickname(nickname)
			.role(Role.USER)
			.build();
		parentRepository.save(parent);
	}

	public TokenInfoDto reGenerateAccessToken(RefreshTokenDto refreshTokenDto) {
		String refreshToken = refreshTokenDto.getRefreshToken();
		if (!jwtTokenProvider.validateToken(refreshToken.substring(7).trim())) {
			throw new ApiException(ErrorCode.INVALID_TOKEN);
		}

		TokenAccountInfoDto.TokenInfo tokenInfoDto = jwtTokenProvider.extractTokenInfoFromJwt(refreshToken);
		String email = tokenInfoDto.getEmail();
		String provider = tokenInfoDto.getProvider();
		return jwtTokenProvider.generateToken(getAuthentication(email, provider));
	}

	private Authentication getAuthentication(String email, String provider) {
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

		Authentication authentication
			= new UsernamePasswordAuthenticationToken(email + "," + provider, null, authorities);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return authentication;
	}

}
