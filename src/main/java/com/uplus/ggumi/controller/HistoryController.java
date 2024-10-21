package com.uplus.ggumi.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uplus.ggumi.config.response.ResponseDto;
import com.uplus.ggumi.config.response.ResponseUtil;
import com.uplus.ggumi.dto.history.HistoryRequestDto;
import com.uplus.ggumi.service.HistoryService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/histories")
public class HistoryController {

	private final HistoryService historyService;

	@PostMapping("/children/{childId}")
	public ResponseDto<Long> saveMbtiHistory(@PathVariable Long childId, @RequestBody HistoryRequestDto requestDto) {
		return ResponseUtil.SUCCESS("MBTI 저장에 성공하였습니다.", historyService.saveMbtiHistory(childId, requestDto));
	}

}
