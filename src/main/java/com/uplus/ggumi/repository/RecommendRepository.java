package com.uplus.ggumi.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.uplus.ggumi.domain.recommend.Recommend;

@Repository
public interface RecommendRepository extends JpaRepository<Recommend, Long> {

	Page<Recommend> findByChildId(Long childId, Pageable pageable);

	@Modifying
	@Query("UPDATE Recommend r SET r.isDeleted = true, r.deletedAt = :now WHERE r.child.id = :childId")
	int markRecommendAsDeletedByChildId(@Param("childId") Long childId, @Param("now") LocalDateTime now);

	Page<Recommend> findAllByIsDeletedAndDeletedAtBefore(boolean isDeleted, LocalDateTime deletedAt, Pageable pageable);
}