package com.uplus.ggumi.repository;

import com.uplus.ggumi.domain.apply.Apply;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplyRepository extends JpaRepository<Apply, Long> {

    boolean existsByPhoneNumber(String phoneNumber);

    @Query(value = "SELECT COUNT(*) FROM apply FOR UPDATE", nativeQuery = true)
    long countWithPessimisticLock();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT COUNT (a) FROM Apply a")
    long countWithLock();

    @Query(value =
            "SELECT * FROM apply " +
                    "WHERE apply_time >= :startTime " +
                    "ORDER BY apply_time ASC LIMIT 100",
            nativeQuery = true)
    List<Apply> findTop100AfterSpecificTime(@Param("startTime") Long startTime);
}
