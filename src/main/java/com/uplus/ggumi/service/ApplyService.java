package com.uplus.ggumi.service;

import com.uplus.ggumi.config.exception.ApiException;
import com.uplus.ggumi.config.exception.ErrorCode;
import com.uplus.ggumi.domain.apply.Apply;
import com.uplus.ggumi.domain.child.Child;
import com.uplus.ggumi.repository.ApplyRepository;
import com.uplus.ggumi.repository.ChildRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplyService {

    private final ApplyRepository applyRepository;
    private final ChildRepository childRepository;

    /* 1단계 기본 Spring Boot + MySQL을 사용한 단일 모듈 구조
     * 응모 요청이 들어오면 MySQL에 바로 save()를 호출해 데이터 저장 */
    public Boolean applyVer1(Long childId, Long applyTime) {

        Child child = childRepository.findById(childId).orElseThrow(() -> new ApiException(ErrorCode.CHILD_NOT_EXIST));

        applyRepository.save(Apply.builder()
                .child(child)
                .applyTime(applyTime)
                .build());

        return true;
    }
}
