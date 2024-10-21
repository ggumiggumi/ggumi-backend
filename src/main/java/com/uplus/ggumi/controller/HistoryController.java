package com.uplus.ggumi.controller;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.uplus.ggumi.dto.history.HistoryRequestDto;

import com.uplus.ggumi.config.response.ResponseDto;
import com.uplus.ggumi.config.response.ResponseUtil;
import com.uplus.ggumi.dto.history.MbtiHistoryPageDto;
import com.uplus.ggumi.service.HistoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "히스토리")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/histories")
public class HistoryController {

	private final HistoryService historyService;

	@Operation(summary = "mbti 검사")
	@PostMapping("/children/{childId}")
	public ResponseDto<Long> saveMbtiHistory(@PathVariable Long childId, @RequestBody HistoryRequestDto requestDto) {
		return ResponseUtil.SUCCESS("MBTI 저장에 성공하였습니다.", historyService.saveMbtiHistory(childId, requestDto));
	}

    @GetMapping("")
    public ResponseDto<MbtiHistoryPageDto> getHistory(HttpServletRequest request) {
        String childId = getCookieValue(request, "ChildId");
        return ResponseUtil.SUCCESS("자녀의 MBTI history 정보를 가져오는 것을 성공하였습니다.", historyService.getChildInfoMbtiHistory(childId));
    }

    private String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}
