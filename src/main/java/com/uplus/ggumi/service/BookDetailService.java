package com.uplus.ggumi.service;

import com.uplus.ggumi.config.exception.ApiException;
import com.uplus.ggumi.config.exception.ErrorCode;
import com.uplus.ggumi.domain.book.Book;
import com.uplus.ggumi.domain.book.MbtiScore;
import com.uplus.ggumi.domain.child.Child;
import com.uplus.ggumi.domain.feedback.Feedback;
import com.uplus.ggumi.domain.feedback.Thumbs;
import com.uplus.ggumi.domain.history.History;
import com.uplus.ggumi.domain.recommend.Recommend;
import com.uplus.ggumi.dto.bookDetail.BookDetailResponseDto;
import com.uplus.ggumi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookDetailService {

    /* 추천 도서 지정 개수 */
    public static final int RECOMMEND_BOOK_COUNT = 8;

    /* 학습률 */
    private static final Double LEARNING_RATE = 0.15;

    private final BookRepository bookRepository;
    private final FeedbackRepository feedbackRepository;
    private final ChildRepository childRepository;
    private final RecommendRepository recommendRepository;
    private final HistoryRepository historyRepository;

    /* 도서 상세 페이지 내용을 보내기 위함 */
    @Transactional
    public BookDetailResponseDto getBookDetail(Long bookId, Long childId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ApiException(ErrorCode.BOOK_NOT_EXIST));
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new ApiException(ErrorCode.CHILD_NOT_EXIST));

        /* 자녀가 해당 도서를 처음 방문했을 경우 새로운 피드백을 생성해준다. */
        Feedback feedback = feedbackRepository.findByBookIdAndChildId(bookId, childId)
                .orElseGet(() -> {
                    Feedback newFeedback = Feedback.builder()
                            .child(child)
                            .book(book)
                            .build();
                    feedbackRepository.save(newFeedback);
                    return newFeedback;
                });

        /* 도서 상세 페이지에 필요한 데이터 */
        return BookDetailResponseDto.builder()
                .title(book.getTitle())
                .author(book.getAuthor())
                .content(book.getContent())
                .bookImage(book.getBook_image())
                .createdAt(book.getCreatedAt())
                .publisher(book.getPublisher())
                .likes(book.getLikes())
                .feedback(feedback.getThumbs())
                .build();
    }

    @Transactional
    public Long updateLike(Long bookId, Long childId) {
        Book book = bookRepository.findByIdWithPessimisticLock(bookId)
                .orElseThrow(() -> new ApiException(ErrorCode.BOOK_NOT_EXIST));

        Feedback feedback = feedbackRepository.findByBookIdAndChildId(bookId, childId)
                .orElseThrow(() -> new ApiException(ErrorCode.FEEDBACK_NOT_EXIST));

        if (feedback.getThumbs() == Thumbs.UP) {
            throw new ApiException(ErrorCode.ALREADY_LIKED);
        }

        feedback.updateThumbs(Thumbs.UP);
        book.incrementLikes();
        calculateChildScoreWithBookScoreWhenClickLike(bookId, childId);
        bookRepository.save(book);

        return 1L;
    }

    @Transactional
    public Long updateHate(Long bookId, Long childId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ApiException(ErrorCode.BOOK_NOT_EXIST));

        Feedback feedback = feedbackRepository.findByBookIdAndChildId(bookId, childId)
                .orElseThrow(() -> new ApiException(ErrorCode.FEEDBACK_NOT_EXIST));

        if (feedback.getThumbs() == Thumbs.DOWN) {
            throw new ApiException(ErrorCode.ALREADY_HATE);
        }

        feedback.updateThumbs(Thumbs.DOWN);
        book.decrementLikes();
        calculateChildScoreWithBookScoreWhenClickHate(bookId, childId);
        bookRepository.save(book);

        return 1L;
    }

    @Transactional
    public Long cancelLike(Long bookId, Long childId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ApiException(ErrorCode.BOOK_NOT_EXIST));

        Feedback feedback = feedbackRepository.findByBookIdAndChildId(bookId, childId)
                .orElseThrow(() -> new ApiException(ErrorCode.FEEDBACK_NOT_EXIST));

        if (feedback.getThumbs() != Thumbs.UP) {
            throw new ApiException(ErrorCode.NOT_LIKED);
        }

        feedback.updateThumbs(Thumbs.UNCHECKED);
        book.decrementLikes();
        bookRepository.save(book);

        return 1L;
    }

    @Transactional
    public Long cancelHate(Long bookId, Long childId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ApiException(ErrorCode.BOOK_NOT_EXIST));

        Feedback feedback = feedbackRepository.findByBookIdAndChildId(bookId, childId)
                .orElseThrow(() -> new ApiException(ErrorCode.FEEDBACK_NOT_EXIST));

        if (feedback.getThumbs() != Thumbs.DOWN) {
            throw new ApiException(ErrorCode.NOT_HATED);
        }

        feedback.updateThumbs(Thumbs.UNCHECKED);
        bookRepository.save(book);
        
        return 1L;
    }

    /* 싫어요, 미선택 -> 좋아요를 눌렀던 시점의 점수 계산 및 기록 저장 */
    private void calculateChildScoreWithBookScoreWhenClickLike(Long bookId, Long childId) {

        /* 점수 계산을 위한 해당 책의 정보와 자녀의 최근 점수 정보를 가져옴 */
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ApiException(ErrorCode.BOOK_NOT_EXIST));

        History history = historyRepository.findTopByChildIdOrderByCreatedAtDesc(childId);

        MbtiScore mbtiScore = book.getMbtiScore();

        /* 점수 계산 */
        double newChildEIScore = getNewChildScoreWhenClickLike(history.getEI(), mbtiScore.getEI());
        double newChildSNScore = getNewChildScoreWhenClickLike(history.getSN(), mbtiScore.getSN());
        double newChildFTScore = getNewChildScoreWhenClickLike(history.getFT(), mbtiScore.getFT());
        double newChildPJScore = getNewChildScoreWhenClickLike(history.getPJ(), mbtiScore.getPJ());

        /* 새로 추가할 히스토리 로그 정보 생성 */
        History newHistory = new History(newChildEIScore, newChildSNScore, newChildFTScore, newChildPJScore,
                history.getChild());

        historyRepository.save(newHistory);
        updateRecommendBooks(newHistory);
    }

    /* 싫어요, 미선택 -> 좋아요를 눌렀을 때의 알고리즘 */
    private double getNewChildScoreWhenClickLike(double child, double book) {
        return Math.max(0, Math.min(1, child + (LEARNING_RATE * (book - child))));
    }

    /* 좋아요 -> 싫어요, 미선택을 눌렀던 시점의 점수 계산 및 기록 저장 */
    private void calculateChildScoreWithBookScoreWhenClickHate(Long bookId, Long childId) {

        /* 점수 계산을 위한 해당 책의 정보와 자녀의 최근 점수 정보를 가져옴 */
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ApiException(ErrorCode.BOOK_NOT_EXIST));

        History history = historyRepository.findTopByChildIdOrderByCreatedAtDesc(childId);

        MbtiScore mbtiScore = book.getMbtiScore();

        /* 점수 계산 */
        double newChildEIScore = getNewChildScoreWhenClickHate(history.getEI(), mbtiScore.getEI());
        double newChildSNScore = getNewChildScoreWhenClickHate(history.getSN(), mbtiScore.getSN());
        double newChildFTScore = getNewChildScoreWhenClickHate(history.getFT(), mbtiScore.getFT());
        double newChildPJScore = getNewChildScoreWhenClickHate(history.getPJ(), mbtiScore.getPJ());

        /* 새로 추가할 히스토리 로그 정보 생성 */
        History newHistory = new History(newChildEIScore, newChildSNScore, newChildFTScore, newChildPJScore,
                history.getChild());

        historyRepository.save(newHistory);
        updateRecommendBooks(newHistory);
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
            MbtiScore mbtiScore = book.getMbtiScore();
            map.put(book,
                    Math.abs(history.getEI() - mbtiScore.getEI())
                            + Math.abs(history.getSN() - mbtiScore.getSN())
                            + Math.abs(history.getPJ() - mbtiScore.getPJ())
                            + Math.abs(history.getFT() - mbtiScore.getFT()));
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
