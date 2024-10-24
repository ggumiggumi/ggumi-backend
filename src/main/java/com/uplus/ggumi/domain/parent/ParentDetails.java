package com.uplus.ggumi.domain.parent;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class ParentDetails implements UserDetails {

	private final Parent parent;

	public ParentDetails(Parent parent) {
		this.parent = parent;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// 필요한 경우 권한을 추가
		return null; // 예시로 null 반환
	}

	@Override
	public String getPassword() {
		return null; // 비밀번호가 필요 없다면 null 반환
	}

	@Override
	public String getUsername() {
		return parent.getEmail(); // 이메일을 사용자 이름으로 사용
	}

	@Override
	public boolean isAccountNonExpired() {
		return true; // 만료되지 않음
	}

	@Override
	public boolean isAccountNonLocked() {
		return true; // 잠겨있지 않음
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true; // 자격 증명이 만료되지 않음
	}

	@Override
	public boolean isEnabled() {
		return true; // 활성화 상태
	}

	public Parent getParent() {
		return this.parent; // Parent 객체 반환
	}
}