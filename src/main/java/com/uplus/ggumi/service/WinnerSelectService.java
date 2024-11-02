package com.uplus.ggumi.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uplus.ggumi.domain.winner.Winner;
import com.uplus.ggumi.dto.winner.winnerResponseDto;
import com.uplus.ggumi.repository.WinnerRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class WinnerSelectService {

	private final WinnerRepository winnerRepository;

	public List<winnerResponseDto> getWinnerList() {
		List<Winner> winners = winnerRepository.findAll();

		return winners.stream()
			.map(winner -> winnerResponseDto.builder()
				.name(maskName(winner.getName())) // 이름 마스킹 처리
				.phoneNum(maskPhoneNum(winner.getPhoneNum())) // 전화번호 마지막 4자리 처리
				.build())
			.collect(Collectors.toList());
	}

	// 이름 중간 글자를 '0'으로 마스킹하는 메서드
	private String maskName(String name) {
		if (name.length() < 2)
			return name;
		StringBuilder maskedName = new StringBuilder(name);
		maskedName.setCharAt(1, '0');
		return maskedName.toString();
	}

	// 전화번호의 마지막 4자리만 반환하는 메서드
	private String maskPhoneNum(String phoneNum) {
		if (phoneNum.length() <= 4)
			return phoneNum;
		return phoneNum.substring(phoneNum.length() - 4);
	}
}