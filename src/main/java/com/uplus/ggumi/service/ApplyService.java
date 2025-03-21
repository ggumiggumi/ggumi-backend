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

    private final ApplyRepository applyRepository;

    /* 1단계 기본 Spring Boot + MySQL 을 사용한 단일 모듈 구조 */
    @Transactional
    public String apply(ApplyRequestDto requestDto) {

        /* 중복 응모 체크 */
        if (applyRepository.existsByPhoneNumber(requestDto.getPhoneNumber())) {
            throw new ApiException(ErrorCode.DUPLICATE_APPLY);
        }

        /* 현재 응모자 수 확인 */
        long count = applyRepository.count();
        if (count >= 100) {
            throw new ApiException(ErrorCode.APPLY_LIMIT_EXCEEDED);
        }

        try {
            /* 서버에서 현재 시간 측정 */
            long currentServerTime = System.currentTimeMillis();

            /* 응모 데이터 저장 */
            Apply apply = Apply.builder()
                    .name(requestDto.getName())
                    .phoneNumber(requestDto.getPhoneNumber())
                    .applyTime(currentServerTime)
                    .build();

            applyRepository.save(apply);
            return "SUCCESS";
        } catch (Exception e) {
            throw new ApiException(ErrorCode.APPLY_FAILED);
        }
    }

}
