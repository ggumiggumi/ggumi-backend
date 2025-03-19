package com.uplus.ggumi.service;

import com.uplus.ggumi.config.exception.ApiException;
import com.uplus.ggumi.config.exception.ErrorCode;
import com.uplus.ggumi.domain.apply.Apply;
import com.uplus.ggumi.dto.apply.ApplyRequestDto;
import com.uplus.ggumi.repository.ApplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplyService {

    private static final long MAX_WINNERS = 100;

    private final ApplyRepository applyRepository;
    private final ApplyPublisherService publisherService;

    /* 1단계 기본 Spring Boot + MySQL을 사용한 단일 모듈 구조 */
    @Transactional
    public String apply(ApplyRequestDto requestDto) {

        /* 1. 중복 응모 체크 */
        if (applyRepository.existsByPhoneNumber(requestDto.getPhoneNumber())) {
            throw new ApiException(ErrorCode.DUPLICATE_APPLY);
        }

        /* 2. 현재 응모자 수 확인 */
        long currentApplicants = applyRepository.countWithPessimisticLock();
        if (currentApplicants >= MAX_WINNERS) {
            throw new ApiException(ErrorCode.APPLY_LIMIT_EXCEEDED);
        }

        /* 3. 응모 정보 저장 */
        try {
            applyRepository.save(Apply.builder()
                    .name(requestDto.getName())
                    .phoneNumber(requestDto.getPhoneNumber())
                    .applyTime(requestDto.getApplyTime())
                    .build());

            return "SUCCESS";

        } catch (Exception e) {
            log.error("Failed to save apply: {}", e.getMessage());
            throw new ApiException(ErrorCode.APPLY_FAILED);
        }
    }

    public String applyWithRedis(ApplyRequestDto requestDto) {
        return publisherService.publishApply(requestDto);
    }

}
