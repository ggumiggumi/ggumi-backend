package com.uplus.ggumi.controller;

import com.uplus.ggumi.config.response.ResponseDto;
import com.uplus.ggumi.config.response.ResponseUtil;
import com.uplus.ggumi.dto.apply.ApplyRequestDto;
import com.uplus.ggumi.service.ApplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/apply")
@RequiredArgsConstructor
public class ApplyController {

    private final ApplyService applyService;

    @PostMapping("/ver1")
    public ResponseDto<Boolean> apply(@RequestBody ApplyRequestDto requestDto) {
        return ResponseUtil.SUCCESS("성공적으로 응모했습니다.", applyService.applyVer1(requestDto.getChildId(), requestDto.getApplyTime()));
    }

}
