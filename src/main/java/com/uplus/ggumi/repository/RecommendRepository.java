package com.uplus.ggumi.repository;

import com.uplus.ggumi.domain.recommend.Recommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendRepository extends JpaRepository<Recommend, Long> {
}
