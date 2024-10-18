package com.uplus.ggumi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uplus.ggumi.config.jwt.JwtTokenProvider;
import com.uplus.ggumi.domain.parent.Parent;
import com.uplus.ggumi.domain.parent.Provider;
import com.uplus.ggumi.dto.token.TokenAccountInfoDto;
import com.uplus.ggumi.repository.ParentRepository;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class ParentService {

	private final ParentRepository parentRepository;
	private final JwtTokenProvider jwtTokenProvider;

	public Parent getAccountByToken(String accessToken) {
		TokenAccountInfoDto.TokenInfo tokenInfoDto = jwtTokenProvider.extractTokenInfoFromJwt(accessToken);
		String email = tokenInfoDto.getEmail();
		Provider provider = Provider.valueOf(tokenInfoDto.getProvider());
		return parentRepository.findByEmailAndProvider(email, provider)
			.orElseThrow(() -> new JwtException("토큰에 해당하는 계정 정보가 없습니다."));
	}

}
