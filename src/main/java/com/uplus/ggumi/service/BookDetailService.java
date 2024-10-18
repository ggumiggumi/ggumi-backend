package com.uplus.ggumi.service;

import com.uplus.ggumi.domain.book.Book;
import com.uplus.ggumi.domain.history.History;
import com.uplus.ggumi.repository.BookDetailRepository;
import com.uplus.ggumi.repository.BookRepository;
import com.uplus.ggumi.repository.HistoryRepository;
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

    @Override
    public Long getTotalLikes(Long key) {
        return redisTemplate.opsForSet().size(LIKE + key.toString());
    }

    public Long calculateChildScoreWithBookScore(Long bookId, Long childId) {

        /* 점수 계산을 위한 해당 책의 정보와 자녀의 최근 점수 정보를 가져옴 */
        Book book = bookRepository.findBookById(bookId);
        History history = historyRepository.findTopByChildIdOrderByCreatedAtDesc(childId);

        /* 점수 계산 */
        double newChildEIScore = getNewChildScore(history.getEI(), book.getEI());
        double newChildSNScore = getNewChildScore(history.getSN(), book.getSN());
        double newChildFTScore = getNewChildScore(history.getFT(), book.getFT());
        double newChildPJScore = getNewChildScore(history.getPJ(), book.getPJ());

        /* 새로 추가할 히스토리 로그 정보 생성 */
        History newHistory = new History(newChildEIScore, newChildSNScore, newChildFTScore, newChildPJScore, history.getChild());

        historyRepository.save(newHistory);

        return newHistory.getId();
    }

    private double getNewChildScore(double child, double book) {
        return child + (LEARNING_RATE * (book - child));
    }

}
