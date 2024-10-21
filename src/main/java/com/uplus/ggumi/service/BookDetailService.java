package com.uplus.ggumi.service;

import com.uplus.ggumi.config.exception.ApiException;
import com.uplus.ggumi.config.exception.ErrorCode;
import com.uplus.ggumi.domain.book.Book;
import com.uplus.ggumi.domain.child.Child;
import com.uplus.ggumi.domain.feedback.Feedback;
import com.uplus.ggumi.domain.feedback.Thumbs;
import com.uplus.ggumi.domain.history.History;
import com.uplus.ggumi.dto.bookDetail.BookDetailResponseDto;
import com.uplus.ggumi.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookDetailService implements BookDetailRepository {

    private static final String LIKE = "like:";
    private static final String HATE = "hate:";

    private static final Double LEARNING_RATE = 0.15;

    private final RedisTemplate<String, Object> redisTemplate;

    private final BookRepository bookRepository;
    private final HistoryRepository historyRepository;
    private final ChildRepository childRepository;
    private final FeedbackRepository feedbackRepository;

    /* 도서 상세 페이지 내용을 보내기 위함 */
    public BookDetailResponseDto getBookDetail(Long bookId, Long childId) {
        Book book = bookRepository.findBookById(bookId);
        Child child = childRepository.findById(childId).orElseThrow(() -> new ApiException(ErrorCode.CHILD_NOT_EXIST));

        /* 자녀가 해당 도서를 처음 방문했을 경우 새로운 피드백을 생성해준다. */
        Feedback feedback = feedbackRepository.findByChildId(childId).orElseGet(() -> {
            Feedback newFeedback = Feedback.builder()
                    .child(child)
                    .book(book)
                    .thumbs(Thumbs.UNCHECKED)
                    .build();
            feedbackRepository.save(newFeedback);
            return newFeedback;
        });

        if (feedback.getThumbs().equals(Thumbs.UP)) {
            redisTemplate.opsForSet().add(LIKE + bookId, childId.toString());
        } else if (feedback.getThumbs().equals(Thumbs.DOWN)) {
            redisTemplate.opsForSet().add(HATE + bookId, childId.toString());
        }

        return BookDetailResponseDto.builder()
                .title(book.getTitle())
                .author(book.getAuthor())
                .content(book.getContent())
                .bookImage(book.getBook_image())
                .createdAt(book.getCreatedAt())
                .publisher(book.getPublisher())
                .feedback(feedback.getThumbs())
                .build();
    }

    @Override
    public Long setLike(Long key, Long value) {
        /* 싫어요에 대한 기록을 지워줌 */
        redisTemplate.opsForSet().remove(HATE + key.toString(), value.toString());

        return redisTemplate.opsForSet().add(LIKE + key, value.toString());
    }

    @Override
    public Long setHate(Long key, Long value) {
        /* 좋아요에 대한 기록을 지워줌 */
        redisTemplate.opsForSet().remove(LIKE + key.toString(), value.toString());

        return redisTemplate.opsForSet().add(HATE + key, value.toString());
    }

    @Override
    public Long undoFeedback(Long key, Long value) {
        /* 좋아요,싫어요 모든 정보를 지워줌 */
        redisTemplate.opsForSet().remove(HATE + key.toString(), value.toString());
        return redisTemplate.opsForSet().remove(LIKE + key, value.toString());
    }

    /* 싫어요, 미선택 -> 좋아요를 눌렀던 시점의 점수 계산 및 기록 저장 */
    public Long calculateChildScoreWithBookScoreWhenClickLike(Long bookId, Long childId) {

        /* 점수 계산을 위한 해당 책의 정보와 자녀의 최근 점수 정보를 가져옴 */
        Book book = bookRepository.findBookById(bookId);
        History history = historyRepository.findTopByChildIdOrderByCreatedAtDesc(childId);

        /* 점수 계산 */
        double newChildEIScore = getNewChildScoreWhenClickLike(history.getEI(), book.getEI());
        double newChildSNScore = getNewChildScoreWhenClickLike(history.getSN(), book.getSN());
        double newChildFTScore = getNewChildScoreWhenClickLike(history.getFT(), book.getFT());
        double newChildPJScore = getNewChildScoreWhenClickLike(history.getPJ(), book.getPJ());

        /* 새로 추가할 히스토리 로그 정보 생성 */
        History newHistory = new History(newChildEIScore, newChildSNScore, newChildFTScore, newChildPJScore, history.getChild());

        historyRepository.save(newHistory);

        return newHistory.getId();
    }

    /* 좋아요 -> 싫어요, 미선택을 눌렀던 시점의 점수 계산 및 기록 저장 */
    public Long calculateChildScoreWithBookScoreWhenClickHate(Long bookId, Long childId) {

        /* 점수 계산을 위한 해당 책의 정보와 자녀의 최근 점수 정보를 가져옴 */
        Book book = bookRepository.findBookById(bookId);
        History history = historyRepository.findTopByChildIdOrderByCreatedAtDesc(childId);

        /* 점수 계산 */
        double newChildEIScore = getNewChildScoreWhenClickHate(history.getEI(), book.getEI());
        double newChildSNScore = getNewChildScoreWhenClickHate(history.getSN(), book.getSN());
        double newChildFTScore = getNewChildScoreWhenClickHate(history.getFT(), book.getFT());
        double newChildPJScore = getNewChildScoreWhenClickHate(history.getPJ(), book.getPJ());

        /* 새로 추가할 히스토리 로그 정보 생성 */
        History newHistory = new History(newChildEIScore, newChildSNScore, newChildFTScore, newChildPJScore, history.getChild());

        historyRepository.save(newHistory);

        return newHistory.getId();
    }

    /* 싫어요, 미선택 -> 좋아요를 눌렀을 때의 알고리즘 */
    private double getNewChildScoreWhenClickLike(double child, double book) {
        return Math.max(0, Math.min(1, child + (LEARNING_RATE * (book - child))));
    }

    /* 좋아요 -> 싫어요, 미선택을 눌렀을 때의 알고리즘 */
    private double getNewChildScoreWhenClickHate(double child, double book) {
        return Math.max(0, Math.min(1, child + (LEARNING_RATE * (child - book))));
    }


}
