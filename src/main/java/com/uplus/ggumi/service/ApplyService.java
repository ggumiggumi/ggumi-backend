package com.uplus.ggumi.service;

import com.uplus.ggumi.config.exception.ApiException;
import com.uplus.ggumi.config.exception.ErrorCode;
import com.uplus.ggumi.domain.apply.Apply;
import com.uplus.ggumi.domain.parent.Parent;
import com.uplus.ggumi.repository.ApplyRepository;
import com.uplus.ggumi.repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplyService {

    private final ApplyRepository applyRepository;
    private final ParentRepository parentRepository;

    private final ParentService parentService;

    /* 1단계 기본 Spring Boot + MySQL을 사용한 단일 모듈 구조
    * 응모 요청이 들어오면 MySQL에 바로 save()를 호출해 데이터 저장 */
    public Boolean applyVer1(String accessToken, Long applyTime) {
        Parent parent = parentService.getAccountByToken(accessToken);

        applyRepository.save(Apply.builder()
                .parent(parent)
                .applyTime(applyTime)
                .build());

        return true;

    }
}
