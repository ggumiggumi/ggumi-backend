package com.uplus.ggumi.service;

import com.uplus.ggumi.config.exception.ApiException;
import com.uplus.ggumi.config.exception.ErrorCode;
import com.uplus.ggumi.domain.child.Child;
import com.uplus.ggumi.domain.history.History;
import com.uplus.ggumi.dto.history.HistoryDtoForHistoryPage;
import com.uplus.ggumi.dto.history.MbtiHistoryPageDto;
import com.uplus.ggumi.repository.ChildRepository;
import com.uplus.ggumi.repository.HistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final ChildRepository childRepository;

    public MbtiHistoryPageDto getChildInfoMbtiHistory(String childId) {
        Long cId = Long.valueOf(childId);

        Child child = childRepository.findById(cId)
                .orElseThrow(() -> new ApiException(ErrorCode.CHILD_NOT_EXIST));
        List<History> histories = historyRepository.findByChildIdLatestHistoryByWeek(cId);
        if(histories.isEmpty()) {
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
}
