package com.uplus.ggumi.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface BookDetailRepository {

    Long setLike(Long key, Long value);

    Long setHate(Long key, Long value);

}
