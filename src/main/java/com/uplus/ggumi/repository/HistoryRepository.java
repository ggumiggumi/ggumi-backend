package com.uplus.ggumi.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.uplus.ggumi.domain.history.History;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {

	@Query("SELECT h FROM History h WHERE h.child.id = :childId ORDER BY h.createdAt DESC LIMIT 1")
	History findTopByChildIdOrderByCreatedAtDesc(@Param("childId") Long childId);

	@Query(value = "SELECT * FROM (" +
		"   SELECT h.*, ROW_NUMBER() OVER (PARTITION BY EXTRACT(YEAR FROM h.created_at), EXTRACT(WEEK FROM h.created_at) "
		+
		"                                ORDER BY h.created_at DESC) as rn " +
		"   FROM history h " +
		"   WHERE h.is_deleted = false " +
		"   AND h.child_id = :childId" +
		") subquery " +
		"WHERE rn = 1 " +
		"ORDER BY subquery.created_at DESC",
		nativeQuery = true)
	List<History> findByChildIdLatestHistoryByWeek(@Param("childId") Long childId);

	@Query(value = "SELECT * FROM history h WHERE h.child_id = :childId AND h.is_deleted = false ORDER BY h.created_at DESC LIMIT 1", nativeQuery = true)
	Optional<History> findLatestHistoryByChildId(@Param("childId") Long childId);

	boolean existsByChildId(Long childId);

	@Modifying
	@Query("UPDATE History h SET h.isDeleted = true, h.deletedAt = :now WHERE h.child.id = :childId")
	int markHistoryAsDeletedByChildId(@Param("childId") Long childId, @Param("now") LocalDateTime now);

	Page<History> findAllByIsDeletedAndDeletedAtBefore(boolean isDeleted, LocalDateTime deletedAt, Pageable pageable);

}

