package com.uplus.ggumi.controller;

import com.uplus.ggumi.config.response.ResponseDto;
import com.uplus.ggumi.config.response.ResponseUtil;
import com.uplus.ggumi.service.BookDetailService;
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

    @GetMapping("/{bookId}/total-likes")
    public ResponseDto<Long> totalLikes(@PathVariable Long bookId) {
        return ResponseUtil.SUCCESS("총 좋아요 개수를 가져왔습니다.", bookDetailService.getTotalLikes(bookId));
    }

    @PostMapping("/{bookId}/like")
    public ResponseDto<Long> clickLikeBook(@PathVariable Long bookId, @RequestBody Map<String, Long> request) {
        return ResponseUtil.SUCCESS("좋아요를 눌렀습니다.", bookDetailService.setLike(bookId, request.get("childId")));
    }

    @PostMapping("/{bookId}/hate")
    public ResponseDto<Long> clickHateBook(@PathVariable Long bookId, @RequestBody Map<String, Long> request) {
        return ResponseUtil.SUCCESS("싫어요를 눌렀습니다.", bookDetailService.setHate(bookId, request.get("childId")));
    }

    @PostMapping("/{bookId}/undo")
    public ResponseDto<Long> undoFeedback(@PathVariable Long bookId, @RequestBody Map<String, Long> request) {
        return ResponseUtil.SUCCESS("피드백을 취소했습니다.", bookDetailService.undoFeedback(bookId, request.get("childId")));
    }

    @PostMapping("/{bookId}/calculation")
    public ResponseDto<Long> calculation(@PathVariable Long bookId, @RequestBody Map<String, Long> request) {
        return ResponseUtil.SUCCESS("책의 점수를 기반으로 자식의 점수를 계산합니다.", bookDetailService.calculateChildScoreWithBookScore(bookId, request.get("childId")));
    }

}
