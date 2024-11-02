package com.uplus.ggumi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.uplus.ggumi.domain.apply.Apply;

@Repository
public interface ApplyRepository extends JpaRepository<Apply, Long> {

	@Query(value = "SELECT * FROM apply WHERE apply_time >= :startTime ORDER BY apply_time ASC LIMIT 100", nativeQuery = true)
	List<Apply> findTop100AfterSpecificTime(@Param("startTime") Long startTime);

	boolean existsByPhoneNumber(String phoneNumber);
}
