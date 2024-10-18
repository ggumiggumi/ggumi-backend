package com.uplus.ggumi.repository;

import com.uplus.ggumi.domain.history.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {

    @Query("SELECT h FROM History h WHERE h.child.id = :childId ORDER BY h.createdAt DESC LIMIT 1")
    History findTopByChildIdOrderByCreatedAtDesc(@Param("childId") Long childId);

}
