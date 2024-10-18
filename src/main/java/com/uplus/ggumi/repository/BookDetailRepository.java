package com.uplus.ggumi.repository;

import org.springframework.stereotype.Service;

@Service
public interface BookDetailRepository {

    Long setLike(Long key, Long value);

    Long setHate(Long key, Long value);

    Long undoFeedback(Long key, Long value);

    Long getTotalLikes(Long key);

}
