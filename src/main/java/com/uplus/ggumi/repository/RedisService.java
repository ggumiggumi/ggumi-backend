package com.uplus.ggumi.repository;

import org.springframework.stereotype.Service;

@Service
public interface RedisService {

    Long setLike(Integer key, Integer value);

    Long setHate(Integer key, Integer value);

    Long undoFeedback(Integer key, Integer value);

    Long getTotalLikes(Integer key);
}
