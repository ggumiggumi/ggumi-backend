package com.uplus.ggumi.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uplus.ggumi.config.response.ResponseDto;
import com.uplus.ggumi.config.response.ResponseUtil;
import com.uplus.ggumi.dto.book.BookResponseDto;
import com.uplus.ggumi.service.BookService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "도서")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books")
public class BookController {

	private final BookService bookService;

	@Operation(summary = "제목으로 도서 검색")
	@GetMapping("/search")
	public ResponseDto<BookResponseDto> searchBooks(@RequestParam String keyword) {
		return ResponseUtil.SUCCESS(keyword + " 검색에 성공하였습니다.", bookService.search(keyword));
	}

}
