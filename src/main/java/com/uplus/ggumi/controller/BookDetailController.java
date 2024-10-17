package com.uplus.ggumi.controller;

import com.uplus.ggumi.config.response.ResponseDto;
import com.uplus.ggumi.config.response.ResponseUtil;
import com.uplus.ggumi.dto.bookDetail.FeedbackRequestDto;
import com.uplus.ggumi.service.BookDetailService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book-detail")
public class BookDetailController {

    private final BookDetailService bookDetailService;

    @GetMapping("/{bookId}/total-likes")
    public ResponseDto<Long> totalLikes(@PathVariable Integer bookId) {
        return ResponseUtil.SUCCESS("총 좋아요 개수를 가져왔습니다.", bookDetailService.getTotalLikes(bookId));
    }

    @PostMapping("/like")
    public ResponseDto<Long> clickLikeBook(@RequestBody FeedbackRequestDto requestDto) {
        return ResponseUtil.SUCCESS("좋아요를 눌렀습니다.", bookDetailService.setLike(requestDto.getBookId(), requestDto.getChildId()));
    }

    @PostMapping("/hate")
    public ResponseDto<Long> clickHateBook(@RequestBody FeedbackRequestDto requestDto) {
        return ResponseUtil.SUCCESS("싫어요를 눌렀습니다.", bookDetailService.setHate(requestDto.getBookId(), requestDto.getChildId()));
    }

    @PostMapping("/undo")
    public ResponseDto<Long> undoFeedback(@RequestBody FeedbackRequestDto requestDto) {
        return ResponseUtil.SUCCESS("피드백을 취소했습니다.", bookDetailService.undoFeedback(requestDto.getBookId(), requestDto.getChildId()));
    }

}
