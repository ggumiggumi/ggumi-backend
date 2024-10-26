package com.uplus.ggumi.service;

import org.springframework.stereotype.Service;

import com.uplus.ggumi.domain.apply.Apply;
import com.uplus.ggumi.dto.apply.ApplyRequestDto;
import com.uplus.ggumi.repository.ApplyRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplyService {

    private final ApplyRepository applyRepository;

    /* 1단계 기본 Spring Boot + MySQL을 사용한 단일 모듈 구조
     * 응모 요청이 들어오면 MySQL에 바로 save()를 호출해 데이터 저장 */
    public Boolean applyVer1(ApplyRequestDto requestDto) {
        applyRepository.save(Apply.builder()
                .name(requestDto.getName())
                .phoneNumber(requestDto.getPhoneNumber())
                .applyTime(requestDto.getApplyTime())
                .build());
        return true;
    }

}
