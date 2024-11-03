package com.uplus.ggumi.controller;

import com.uplus.ggumi.config.response.ResponseDto;
import com.uplus.ggumi.config.response.ResponseUtil;
import com.uplus.ggumi.dto.book.MainBookResponseDto;
import com.uplus.ggumi.service.BookService;
import com.uplus.ggumi.service.OpenAIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
public class MainController {

    private final BookService bookService;


    @Autowired
    private OpenAIService openAiService; // OpenAiService 주입

    @PostMapping("/books")
    public ResponseDto<MainBookResponseDto> getBooks(@RequestParam int page, @RequestBody Map<String, Long> request) {
        return ResponseUtil.SUCCESS("책 정보들을 성공적으로 가져왔습니다.", bookService.getBooks(request.get("childId"), page));
    }

    @GetMapping("/popularity")
    public ResponseDto<MainBookResponseDto> getPopularBooks(@RequestParam int page) {
            return ResponseUtil.SUCCESS("인기있는 책 리스트",bookService.getPopularBooks(page));
    }

    @PostMapping("/gpt")
    public ResponseDto<String> gpt(@RequestParam(name = "userInput") String userInput) throws Exception {
        System.out.println(userInput);
        return ResponseUtil.SUCCESS("지피티 응답",openAiService.getChatGPTResponse(userInput));

    }

}
