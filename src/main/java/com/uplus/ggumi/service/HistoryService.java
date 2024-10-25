package com.uplus.ggumi.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uplus.ggumi.config.exception.ApiException;
import com.uplus.ggumi.config.exception.ErrorCode;
import com.uplus.ggumi.domain.child.Child;
import com.uplus.ggumi.domain.history.History;
import com.uplus.ggumi.dto.history.HistoryDtoForHistoryPage;
import com.uplus.ggumi.dto.history.HistoryRequestDto;
import com.uplus.ggumi.dto.history.MBTIType;
import com.uplus.ggumi.dto.history.MbtiHistoryPageDto;
import com.uplus.ggumi.dto.history.MyChildMBTITypeDto;
import com.uplus.ggumi.repository.ChildRepository;
import com.uplus.ggumi.repository.HistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class HistoryService {

	private final HistoryRepository historyRepository;
	private final ChildRepository childRepository;

	public MbtiHistoryPageDto getChildInfoMbtiHistory(String childId) {
		Long cId = Long.valueOf(childId);

		Child child = childRepository.findById(cId)
			.orElseThrow(() -> new ApiException(ErrorCode.CHILD_NOT_EXIST));
		List<History> histories = historyRepository.findByChildIdLatestHistoryByWeek(cId);
		if (histories.isEmpty()) {
			throw new ApiException(ErrorCode.HISTORY_NOT_EXIST);
		}

		// History -> HistoryDtoForHistoryPage로 변환
		List<HistoryDtoForHistoryPage> historyDtos = histories.stream()
			.map(history -> HistoryDtoForHistoryPage.builder()
				.EI(history.getEI())
				.SN(history.getSN())
				.FT(history.getFT())
				.PJ(history.getPJ())
				.createdAt(history.getCreatedAt()) // 생성 시간
				.build()
			)
			.toList();

		return MbtiHistoryPageDto.builder()
			.name(child.getName())
			.profileCode(child.getProfileCode())
			.histories(historyDtos)
			.build();
	}

	public Long saveMbtiHistory(Long childId, HistoryRequestDto requestDto) {
		Child child = childRepository.findById(childId)
			.orElseThrow(() -> new ApiException(ErrorCode.CHILD_NOT_EXIST));

		History history = History.builder()
			.child(child)
			.EI(requestDto.getEI() / 100)
			.SN(requestDto.getSN() / 100)
			.FT(requestDto.getFT() / 100)
			.PJ(requestDto.getPJ() / 100)
			.isDeleted(false)
			.build();
		return historyRepository.save(history).getId();
	}

	public MyChildMBTITypeDto getChildMbtiType(String cId) {
		Long childId = Long.valueOf(cId);

		Child child = childRepository.findById(childId)
			.orElseThrow(() -> new ApiException(ErrorCode.CHILD_NOT_EXIST));
		Pageable pageable = PageRequest.of(0, 1);
		History latestHistory = historyRepository.findLatestHistoryByChildId(childId, pageable)
			.stream()
			.findFirst()
			.orElseThrow(() -> new ApiException(ErrorCode.HISTORY_NOT_EXIST));

		// MBTI 계산
		StringBuilder mbtiBuilder = new StringBuilder();
		mbtiBuilder.append(latestHistory.getEI() >= 0.5 ? "E" : "I");
		mbtiBuilder.append(latestHistory.getSN() >= 0.5 ? "S" : "N");
		mbtiBuilder.append(latestHistory.getFT() >= 0.5 ? "F" : "T");
		mbtiBuilder.append(latestHistory.getPJ() >= 0.5 ? "P" : "J");

		int intE = (int)(latestHistory.getEI() * 100);
		int intS = (int)(latestHistory.getSN() * 100);
		int intF = (int)(latestHistory.getFT() * 100);
		int intP = (int)(latestHistory.getPJ() * 100);

		// Enum을 통해 MBTI 정보 매핑
		MBTIType mbtiType = MBTIType.valueOf(mbtiBuilder.toString());
		String mbtiDesc = mbtiType.getDescription();
		String[] mbtiTags = mbtiType.getTags();

		return MyChildMBTITypeDto.builder()
			.name(child.getName())
			.E(intE)
			.I(100 - intE)
			.S(intS)
			.N(100 - intS)
			.F(intF)
			.T(100 - intF)
			.P(intP)
			.J(100 - intP)
			.mbtiType(mbtiType)
			.mbtiDesc(mbtiDesc)
			.mbtiTags(mbtiTags)
			.build();
	}
}
