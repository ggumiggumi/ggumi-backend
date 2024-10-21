package com.uplus.ggumi.controller;

import com.uplus.ggumi.config.response.ResponseDto;
import com.uplus.ggumi.config.response.ResponseUtil;
import com.uplus.ggumi.dto.bookDetail.BookDetailResponseDto;
import com.uplus.ggumi.service.BookDetailService;
import com.uplus.ggumi.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book-detail")
public class BookDetailController {

    private final BookDetailService bookDetailService;
    private final FeedbackService feedbackService;

    @PostMapping("/{bookId}")
    public ResponseDto<BookDetailResponseDto> getBookDetail(@PathVariable Long bookId, @RequestBody Map<String, Long> request) {
        return ResponseUtil.SUCCESS("책 정보를 성공적으로 가져왔습니다.", bookDetailService.getBookDetail(bookId, request.get("childId")));
    }

    @PostMapping("/{bookId}/like")
    public ResponseDto<Long> clickLikeBook(@PathVariable Long bookId, @RequestBody Map<String, Long> request) {
        return ResponseUtil.SUCCESS("좋아요를 눌렀습니다.", bookDetailService.setLike(bookId, request.get("childId")));
    }

    @PostMapping("/{bookId}/hate")
    public ResponseDto<Long> clickHateBook(@PathVariable Long bookId, @RequestBody Map<String, Long> request) {
        return ResponseUtil.SUCCESS("싫어요를 눌렀습니다.", bookDetailService.setHate(bookId, request.get("childId")));
    }

    @PostMapping("/{bookId}/undo-like")
    public ResponseDto<Long> undoLike(@PathVariable Long bookId, @RequestBody Map<String, Long> request) {
        return ResponseUtil.SUCCESS("좋아요를 취소했습니다.", bookDetailService.undoLike(bookId, request.get("childId")));
    }

    @PostMapping("/{bookId}/undo-hate")
    public ResponseDto<Long> undoHate(@PathVariable Long bookId, @RequestBody Map<String, Long> request) {
        return ResponseUtil.SUCCESS("싫어요를 취소했습니다.", bookDetailService.undoHate(bookId, request.get("childId")));
    }

    @PostMapping("/{bookId}/calculation-like")
    public ResponseDto<Long> calculationWhenClickLike(@PathVariable Long bookId, @RequestBody Map<String, Long> request) {
        return ResponseUtil.SUCCESS("책의 점수를 기반으로 점수를 계산합니다.", bookDetailService.calculateChildScoreWithBookScoreWhenClickLike(bookId, request.get("childId")));
    }

    @PostMapping("/{bookId}/calculation-hate")
    public ResponseDto<Long> calculationWhenClickHate(@PathVariable Long bookId, @RequestBody Map<String, Long> request) {
        return ResponseUtil.SUCCESS("책의 점수를 기반으로 점수를 계산합니다.", bookDetailService.calculateChildScoreWithBookScoreWhenClickHate(bookId, request.get("childId")));
    }

    @PostMapping("/{bookId}/feedback")
    public ResponseDto<Long> updateFeedback(@PathVariable Long bookId, @RequestBody Map<String, Long> request) {
        return ResponseUtil.SUCCESS("피드백 정보를 저장했습니다.", feedbackService.saveFeedbackStatus(bookId, request.get("childId")));
    }
}
