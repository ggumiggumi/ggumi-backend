package com.uplus.ggumi.service;

import com.uplus.ggumi.config.exception.ApiException;
import com.uplus.ggumi.config.exception.ErrorCode;
import com.uplus.ggumi.domain.book.Book;
import com.uplus.ggumi.domain.child.Child;
import com.uplus.ggumi.domain.feedback.Feedback;
import com.uplus.ggumi.domain.feedback.Thumbs;
import com.uplus.ggumi.domain.history.History;
import com.uplus.ggumi.domain.recommend.Recommend;
import com.uplus.ggumi.dto.bookDetail.BookDetailResponseDto;
import com.uplus.ggumi.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookDetailService implements BookDetailRepository {

    /* 추천 도서 지정 개수 */
    public static final int RECOMMEND_BOOK_COUNT = 8;
    /* 총 좋아요 개수 Key 값 */
    private static final String TOTAL_LIKES = "totalLikes:";
    /* 좋아요 Key 값 */
    private static final String LIKE = "like:";
    /* 싫어요 Key 값 */
    private static final String HATE = "hate:";
    /* 학습률 */
    private static final Double LEARNING_RATE = 0.15;

    private final RedisTemplate<String, Object> redisTemplate;

    private final BookRepository bookRepository;
    private final HistoryRepository historyRepository;
    private final ChildRepository childRepository;
    private final FeedbackRepository feedbackRepository;
    private final RecommendRepository recommendRepository;

    /* 도서 상세 페이지 내용을 보내기 위함 */
    public BookDetailResponseDto getBookDetail(Long bookId, Long childId) {
        Book book = bookRepository.findBookById(bookId);
        Child child = childRepository.findById(childId).orElseThrow(() -> new ApiException(ErrorCode.CHILD_NOT_EXIST));

        /* 자녀가 해당 도서를 처음 방문했을 경우 새로운 피드백을 생성해준다. */
        Feedback feedback = feedbackRepository.findByBookIdAndChildId(bookId, childId).orElseGet(() -> {
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

        /* 도서 상세 페이지에 필요한 데이터 */
        return BookDetailResponseDto.builder()
                .title(book.getTitle())
                .author(book.getAuthor())
                .content(book.getContent())
                .bookImage(book.getBook_image())
                .createdAt(book.getCreatedAt())
                .publisher(book.getPublisher())
                .likes(getTotalLikes(book, bookId))
                .feedback(feedback.getThumbs())
                .build();
    }

    /* 해당 도서의 총 좋아요 수에 대한 Cache Aside 작업 */
    private Integer getTotalLikes(Book book, Long bookId) {
        Object cachedLikes = redisTemplate.opsForValue().get(TOTAL_LIKES + bookId);
        if (cachedLikes != null) {
            return Integer.parseInt(cachedLikes.toString());
        }

        redisTemplate.opsForValue().set(TOTAL_LIKES + bookId, String.valueOf(book.getLikes()));

        return book.getLikes();
    }

    @Override
    public Long setLike(Long key, Long value) {
        /* 싫어요에 대한 기록을 지워줌 */
        redisTemplate.opsForSet().remove(HATE + key.toString(), value.toString());
        /* 총 좋아요 개수 +1 */
        redisTemplate.opsForValue().increment(TOTAL_LIKES + key);
        return redisTemplate.opsForSet().add(LIKE + key, value.toString());
    }

    @Override
    public Long setHate(Long key, Long value) {
        /* 좋아요에 대한 기록을 지워줌 */
        redisTemplate.opsForSet().remove(LIKE + key.toString(), value.toString());
        /* 총 좋아요 개수 -1, 단 좋아요의 개수가 0 미만으로 가면 안됨. */
        if (Integer.parseInt(Objects.requireNonNull(redisTemplate.opsForValue().get(TOTAL_LIKES + key)).toString()) > 0) {
            redisTemplate.opsForValue().decrement(TOTAL_LIKES + key);
        }
        return redisTemplate.opsForSet().add(HATE + key, value.toString());
    }

    public Long undoLike(Long key, Long value) {
        /* 총 좋아요 개수 -1, 단 좋아요의 개수가 0 미만으로 가면 안됨. */
        if (Integer.parseInt(Objects.requireNonNull(redisTemplate.opsForValue().get(TOTAL_LIKES + key)).toString()) > 0) {
            redisTemplate.opsForValue().decrement(TOTAL_LIKES + key);
        }
        /* 좋아요 정보를 지워줌 */
        return redisTemplate.opsForSet().remove(LIKE + key.toString(), value.toString());
    }

    public Long undoHate(Long key, Long value) {
        /* 싫어요 정보를 지워줌 */
        return redisTemplate.opsForSet().remove(HATE + key.toString(), value.toString());
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
        updateRecommendBooks(newHistory);

        return newHistory.getId();
    }

    /* 싫어요, 미선택 -> 좋아요를 눌렀을 때의 알고리즘 */
    private double getNewChildScoreWhenClickLike(double child, double book) {
        return Math.max(0, Math.min(1, child + (LEARNING_RATE * (book - child))));
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
        updateRecommendBooks(newHistory);

        return newHistory.getId();
    }

    /* 좋아요 -> 싫어요, 미선택을 눌렀을 때의 알고리즘 */
    private double getNewChildScoreWhenClickHate(double child, double book) {
        return Math.max(0, Math.min(1, child + (LEARNING_RATE * (child - book))));
    }

    /* 자녀 성향 점수가 변하는 시점에 추천 도서 목록을 업데이트 하는 메서드 */
    private void updateRecommendBooks(History history) {
        List<Book> books = bookRepository.findAll();

        Map<Book, Double> map = new HashMap<>();
        for (Book book : books) {
            map.put(book,
                    Math.abs(history.getEI() - book.getEI())
                            + Math.abs(history.getSN() - book.getSN())
                            + Math.abs(history.getPJ() - book.getPJ())
                            + Math.abs(history.getFT() - book.getFT()));
        }

        /* Stream API를 사용해 값(Value) 기준으로 오름차순 정렬한 후 Key 값만 리스트로 수집 */
        List<Book> sortedKeys = map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())  // Value 기준으로 정렬
                .map(Map.Entry::getKey)                // Key 값만 추출
                .toList();                             // 리스트로 수집

        /* 새롭게 업데이트 한 책들을 넣기 전에 해당 테이블의 책들을 비워준다. */
        recommendRepository.deleteAll();

        /* 상위 RECOMMEND_BOOK_COUNT 권의 책만 추천 테이블에 넣어준다. */
        int count = 0;
        for (Book recommendBook : sortedKeys) {
            if (count == RECOMMEND_BOOK_COUNT) {
                return;
            }
            recommendRepository.save(Recommend.builder()
                    .book(recommendBook)
                    .child(history.getChild())
                    .build());
            count++;
        }
    }
}
