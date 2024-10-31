package com.uplus.ggumi.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.uplus.ggumi.domain.feedback.Feedback;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

	Optional<Feedback> findByBookIdAndChildId(Long bookId, Long childId);

	@Modifying
	@Query("UPDATE Feedback f SET f.isDeleted = true, f.deletedAt = :now WHERE f.child.id = :childId")
	int markFeedbackAsDeletedByChildId(@Param("childId") Long childId, @Param("now") LocalDateTime now);

	Page<Feedback> findAllByIsDeletedAndDeletedAtBefore(boolean isDeleted, LocalDateTime deletedAt, Pageable pageable);
}