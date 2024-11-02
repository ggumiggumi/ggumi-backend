package com.uplus.ggumi.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uplus.ggumi.domain.apply.Apply;
import com.uplus.ggumi.domain.winner.Winner;
import com.uplus.ggumi.dto.winner.winnerResponseDto;
import com.uplus.ggumi.repository.ApplyRepository;
import com.uplus.ggumi.repository.WinnerRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class WinnerSelectService {

	private final WinnerRepository winnerRepository;
	private final ApplyRepository applyRepository;

	public List<winnerResponseDto> getWinnerList() {
		LocalDate yesterday = LocalDate.now().minusDays(1);
		long startOfYesterday = yesterday.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
		long endOfYesterday = yesterday.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

		// applyTime이 전날 범위에 속하는 Winner 데이터 가져오기
		List<Winner> winners = winnerRepository.findAll().stream()
			.filter(winner -> winner.getApplyTime() >= startOfYesterday && winner.getApplyTime() <= endOfYesterday)
			.toList();

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

	public void saveTodayWinner() {
		// 13시 기준 오늘의 시작 시간
		LocalDate today = LocalDate.now();
		long startTime = today.atTime(LocalTime.of(13, 0)).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

		// 2일 전의 00시 타임스탬프 계산하여 해당 날짜 이전의 Winner 데이터 삭제
		LocalDate twoDaysAgo = today.minusDays(2);
		long cutoffTime = twoDaysAgo.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
		winnerRepository.deleteByApplyTimeBefore(cutoffTime);

		// 13시 이후의 상위 100명 신청자 가져오기
		List<Apply> selectedApplicants = applyRepository.findTop100AfterSpecificTime(startTime);

		// Winner 엔티티로 변환하여 저장
		List<Winner> winners = selectedApplicants.stream()
			.map(apply -> Winner.builder()
				.name(apply.getName())
				.phoneNum(apply.getPhoneNumber())
				.applyTime(apply.getApplyTime())
				.build())
			.toList();

		winnerRepository.saveAll(winners);
	}
}
