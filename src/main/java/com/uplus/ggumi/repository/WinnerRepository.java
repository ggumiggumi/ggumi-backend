package com.uplus.ggumi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uplus.ggumi.domain.winner.Winner;

@Repository
public interface WinnerRepository extends JpaRepository<Winner, Long> {

	@Modifying
	@Transactional
	@Query("DELETE FROM Winner w WHERE w.applyTime < :cutoffTime")
	void deleteByApplyTimeBefore(@Param("cutoffTime") Long cutoffTime);
}
