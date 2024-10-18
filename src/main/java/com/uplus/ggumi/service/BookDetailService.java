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

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookDetailService implements BookDetailRepository {

    public static final String LIKE = "like:";
    public static final String HATE = "hate:";

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
        /* 좋아요/싫어요 모든 정보를 지워줌 */
        redisTemplate.opsForSet().remove(HATE + key.toString(), value.toString());
        return redisTemplate.opsForSet().remove(LIKE + key, value.toString());
    }

    @Override
    public Long getTotalLikes(Long key) {
        return redisTemplate.opsForSet().size(LIKE + key.toString());
    }

    public Long calculateChildScoreWithBookScore(Long bookId, Long childId) {

        Book book = bookRepository.findBookById(bookId);
        History history = historyRepository.findTopByChildIdOrderByCreatedAtDesc(childId);

        log.info(history.getId().toString());
        log.info(book.getTitle());

        return 1L;
    }

}
