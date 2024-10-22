package com.uplus.ggumi.controller;

import com.uplus.ggumi.config.response.ResponseDto;
import com.uplus.ggumi.config.response.ResponseUtil;
import com.uplus.ggumi.dto.book.MainBookResponseDto;
import com.uplus.ggumi.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
public class MainController {

    private final BookService bookService;

    @PostMapping("/books")
    public ResponseDto<MainBookResponseDto> getBooks(@RequestBody Map<String, Integer> request) {
        return ResponseUtil.SUCCESS("책 정보들을 성공적으로 가져왔습니다.", bookService.getBooks(request.get("childId").longValue(), request.get("page")));
    }
}
