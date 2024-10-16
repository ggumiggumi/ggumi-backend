package com.uplus.ggumi.controller;

import com.uplus.ggumi.config.response.ResponseDto;
import com.uplus.ggumi.config.response.ResponseUtil;
import com.uplus.ggumi.dto.child.ChildProfileRequestDto;
import com.uplus.ggumi.dto.child.ChildProfileResponseDto;
import com.uplus.ggumi.service.ChildManagerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/children")
public class ChildController {

    private final ChildManagerService childManagerService;

    // {parentId}가 들어있는 부분은 추후 jwt 활용 코드로 수정 필요

    @PostMapping("/{parentId}")
    public ResponseDto<Long> createChild(@PathVariable Long parentId, @RequestBody ChildProfileRequestDto requestDto) {
        return ResponseUtil.SUCCESS("자녀 프로필 생성에 성공하였습니다.", childManagerService.createChildProfile(parentId, requestDto));
    }

    @GetMapping("/list/{parentId}")
    public ResponseDto<List<ChildProfileResponseDto>> getChildren(@PathVariable Long parentId) {
        return ResponseUtil.SUCCESS("자녀 프로필 정보 리스트를 가져오는 것을 성공하였습니다.", childManagerService.getChildProfileList(parentId));
    }

    @GetMapping("/{childId}")
    public ResponseDto<ChildProfileResponseDto> getChild(@PathVariable Long childId) {
        return ResponseUtil.SUCCESS("자녀 프로필 정보를 가져오는 것을 성공하였습니다.", childManagerService.getChildProfile(childId));
    }

    @PutMapping("/{childId}")
    public ResponseDto<Long> updateChild(@PathVariable Long childId, @RequestBody ChildProfileRequestDto requestDto) {
        return ResponseUtil.SUCCESS("자녀 프로필 업데이트에 성공하였습니다.", childManagerService.updateChildProfile(childId, requestDto));
    }

    @GetMapping("/{parentId}/can-create")
    public ResponseDto<Integer> allowChildProfileCreation(@PathVariable Long parentId) {
        return ResponseUtil.SUCCESS("자녀 프로필 생성이 가능합니다.", childManagerService.checkChildCreationLimit(parentId)); // 실제 화면 이동 로직 필요
    }
}
