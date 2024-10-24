package com.uplus.ggumi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.uplus.ggumi.config.response.ResponseDto;
import com.uplus.ggumi.config.response.ResponseUtil;
import com.uplus.ggumi.dto.book.BookManagementRequestDto;
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

	@Operation(summary = "도서 정보 등록")
	@PostMapping("/create")
	public ResponseDto<Long> createBook(
		@RequestPart BookManagementRequestDto requestDto, @RequestPart MultipartFile imageFile) {
		return ResponseUtil.SUCCESS("도서 정보 등록에 성공하였습니다.", bookService.createBook(requestDto, imageFile));
	}

	@Operation(summary = "도서 정보 수정")
	@PutMapping("/update/{bookId}")
	public ResponseDto<Long> updateBook(
		@PathVariable Long bookId, @RequestPart BookManagementRequestDto requestDto,
		@RequestPart MultipartFile imageFile) {
		return ResponseUtil.SUCCESS("도서 정보 수정에 성공하였습니다.", bookService.updateBook(bookId, requestDto, imageFile));
	}

}
