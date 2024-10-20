package com.uplus.ggumi.service;

import com.uplus.ggumi.domain.feedback.Feedback;
import com.uplus.ggumi.domain.feedback.Thumbs;
import com.uplus.ggumi.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final FeedbackRepository feedbackRepository;

    private static final String LIKE = "like:";
    private static final String HATE = "hate:";

    /* 페이지를 벗어나는 시점 (다른 페이지 이동, 뒤로가기, 새로고침)에 피드백 정보 RDB 영구 저장 */
    public Long saveFeedbackStatus(Long bookId, Long childId) {

        Feedback feedback = feedbackRepository.findByChildId(childId);

        Boolean isLiked = redisTemplate.opsForSet().isMember(LIKE + bookId, childId.toString());
        Boolean isHated = redisTemplate.opsForSet().isMember(HATE + bookId, childId.toString());

        if (Boolean.TRUE.equals(isLiked)) {
            feedback.updateThumbs(Thumbs.UP);
        } else if (Boolean.TRUE.equals(isHated)) {
            feedback.updateThumbs(Thumbs.DOWN);
        } else {
            feedback.updateThumbs(Thumbs.UNCHECKED);
        }

        feedbackRepository.save(feedback);

        return feedback.getId();
    }
}
