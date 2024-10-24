package com.uplus.ggumi.config.jwt;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.uplus.ggumi.config.exception.ApiException;
import com.uplus.ggumi.config.exception.ErrorCode;
import com.uplus.ggumi.domain.parent.Parent;
import com.uplus.ggumi.domain.parent.ParentDetails;
import com.uplus.ggumi.domain.parent.Provider;
import com.uplus.ggumi.dto.token.TokenAccountInfoDto;
import com.uplus.ggumi.dto.token.TokenInfoDto;
import com.uplus.ggumi.repository.ParentRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {

	private final Key key;
	private final ParentRepository parentRepository;
	private final int ACCESSTOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; //일주일
	private final int REFRESHTOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 21;

	@Autowired
	public JwtTokenProvider(@Value("${jwt.secret}") String secret, ParentRepository parentRepository) {
		this.key = Keys.hmacShaKeyFor(secret.getBytes());
		this.parentRepository = parentRepository;
	}

	public TokenInfoDto generateToken(Authentication authentication) {
		String accessToken = createToken(authentication, ACCESSTOKEN_EXPIRATION_TIME);
		String refreshToken = createToken(authentication, REFRESHTOKEN_EXPIRATION_TIME);

		return TokenInfoDto.builder()
			.grantType("Bearer")
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

	private String getAuthorities(Authentication authentication) {
		return authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));
	}

	private String createToken(Authentication authentication, int expirationTime) {
		Date tokenExpiration = new Date(getNowTime() + expirationTime);

		return Jwts.builder()
			.setSubject(authentication.getName())
			.claim("auth", getAuthorities(authentication))
			.setExpiration(tokenExpiration)
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	public long getNowTime() {
		return (new Date()).getTime();
	}

	public Authentication getAuthentication(String accessToken) {
		Claims claims = parseClaims(accessToken);

		if (claims.get("auth") == null) {
			throw new ApiException(ErrorCode.ACCESS_DENIED);
		}

		Collection<? extends GrantedAuthority> authorities =
			Arrays.stream(claims.get("auth").toString().split(","))
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());

		String[] subjectParts = claims.getSubject().split(",");
		String email = subjectParts[0];
		Provider provider = subjectParts.length > 1 ? Provider.valueOf(subjectParts[1]) : null;

		Parent parent = parentRepository.findByEmailAndProvider(email, provider)
			.orElseThrow(() -> new ApiException(ErrorCode.PARENT_NOT_EXIST));

		ParentDetails parentDetails = new ParentDetails(parent);

		return new UsernamePasswordAuthenticationToken(parentDetails, accessToken, authorities);
	}

	public boolean validateToken(String accessToken) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken);
			return true;
		} catch (SecurityException | MalformedJwtException e) {
			throw new ApiException(ErrorCode.INVALID_TOKEN);
		} catch (ExpiredJwtException e) {
			throw new ApiException(ErrorCode.EXPIRED_TOKEN);
		} catch (UnsupportedJwtException e) {
			throw new ApiException(ErrorCode.UNSUPPORTED_TOKEN);
		} catch (IllegalArgumentException e) {
			throw new ApiException(ErrorCode.UNKNOWN_ERROR);
		}
	}

	public TokenAccountInfoDto.TokenInfo extractTokenInfoFromJwt(String token) {
		if (token.startsWith("Bearer ")) {
			String resolvedToken = token.substring(7).trim();
			Claims claims = parseClaims(resolvedToken);
			String[] subjectParts = claims.getSubject().split(",");
			if (subjectParts.length > 1) {
				String email = subjectParts[0];
				String provider = subjectParts[1];
				return TokenAccountInfoDto.TokenInfo.builder().email(email).provider(provider).build();
			}
		}

		return null;
	}

	private Claims parseClaims(String accessToken) {
		try {
			return Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(accessToken)
				.getBody();
		} catch (ExpiredJwtException e) {
			throw new ApiException(ErrorCode.EXPIRED_TOKEN);
		}
	}

}
