package com.uplus.ggumi.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uplus.ggumi.config.response.ResponseDto;
import com.uplus.ggumi.config.response.ResponseUtil;
import com.uplus.ggumi.dto.winner.winnerResponseDto;
import com.uplus.ggumi.service.WinnerSelectService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/winner")
public class WinnerController {

	private final WinnerSelectService winnerSelectService;

	@GetMapping("/list")
	public ResponseDto<List<winnerResponseDto>> getWinnerList() {
		return ResponseUtil.SUCCESS("당첨자 조회를 성공하였습니다.",
			winnerSelectService.getWinnerList());
	}
}
