package com.uplus.ggumi.repository;

import com.uplus.ggumi.domain.history.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {

	@Query("SELECT h FROM History h WHERE h.child.id = :childId ORDER BY h.createdAt DESC LIMIT 1")
	History findTopByChildIdOrderByCreatedAtDesc(@Param("childId") Long childId);


	@Query(value = "SELECT * FROM (" +
			"   SELECT h.*, ROW_NUMBER() OVER (PARTITION BY EXTRACT(YEAR FROM h.created_at), EXTRACT(WEEK FROM h.created_at) " +
			"                                ORDER BY h.created_at DESC) as rn " +
			"   FROM history h " +
			"   WHERE h.is_deleted = false " +
			"   AND h.child_id = :childId" +
			") subquery " +
			"WHERE rn = 1 " +
			"ORDER BY subquery.created_at DESC",
			nativeQuery = true)
	List<History> findByChildIdLatestHistoryByWeek(@Param("childId") Long childId);

	boolean existsByChildId(Long childId);
}

