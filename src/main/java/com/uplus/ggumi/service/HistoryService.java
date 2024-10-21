package com.uplus.ggumi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uplus.ggumi.config.exception.ApiException;
import com.uplus.ggumi.config.exception.ErrorCode;
import com.uplus.ggumi.domain.child.Child;
import com.uplus.ggumi.domain.history.History;
import com.uplus.ggumi.dto.history.HistoryRequestDto;
import com.uplus.ggumi.repository.ChildRepository;
import com.uplus.ggumi.repository.HistoryRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class HistoryService {

	private final HistoryRepository historyRepository;
	private final ChildRepository childRepository;

	public Long saveMbtiHistory(Long childId, HistoryRequestDto requestDto) {
		Child child = childRepository.findById(childId)
			.orElseThrow(() -> new ApiException(ErrorCode.CHILD_NOT_EXIST));

		History history = History.builder()
			.child(child)
			.EI(requestDto.getEI())
			.SN(requestDto.getSN())
			.FT(requestDto.getFT())
			.PJ(requestDto.getPJ())
			.isDeleted(false)
			.build();
		return historyRepository.save(history).getId();
	}
}
