package com.uplus.ggumi.repository;

import com.uplus.ggumi.domain.feedback.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    Feedback findByChildId(Long childId);

}
