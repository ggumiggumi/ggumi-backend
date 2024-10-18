package com.uplus.ggumi.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uplus.ggumi.config.response.ResponseDto;
import com.uplus.ggumi.config.response.ResponseUtil;
import com.uplus.ggumi.dto.child.ChildProfileRequestDto;
import com.uplus.ggumi.dto.child.ChildProfileResponseDto;
import com.uplus.ggumi.service.ChildManagerService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/children")
public class ChildController {

    private final ChildManagerService childManagerService;

    @PostMapping("")
    public ResponseDto<Long> createChild(HttpServletRequest request, @RequestBody ChildProfileRequestDto requestDto) {
        return ResponseUtil.SUCCESS("자녀 프로필 생성에 성공하였습니다.", childManagerService.createChildProfile(request.getHeader("Authorization"), requestDto));
    }

    @GetMapping("/list")
    public ResponseDto<List<ChildProfileResponseDto>> getChildren(HttpServletRequest request) {
        return ResponseUtil.SUCCESS("자녀 프로필 정보 리스트를 가져오는 것을 성공하였습니다.", childManagerService.getChildProfileList(request.getHeader("Authorization")));
    }

    @GetMapping("/{childId}")
    public ResponseDto<ChildProfileResponseDto> getChild(@PathVariable Long childId) {
        return ResponseUtil.SUCCESS("자녀 프로필 정보를 가져오는 것을 성공하였습니다.", childManagerService.getChildProfile(childId));
    }

    @PutMapping("/{childId}")
    public ResponseDto<Long> updateChild(@PathVariable Long childId, @RequestBody ChildProfileRequestDto requestDto) {
        return ResponseUtil.SUCCESS("자녀 프로필 업데이트에 성공하였습니다.", childManagerService.updateChildProfile(childId, requestDto));
    }

    @GetMapping("/can-create")
    public ResponseDto<Integer> allowChildProfileCreation(HttpServletRequest request) {
        return ResponseUtil.SUCCESS("자녀 프로필 생성이 가능합니다.", childManagerService.checkChildCreationLimit(request.getHeader("Authorization"))); // 실제 화면 이동 로직 필요
    }
}
