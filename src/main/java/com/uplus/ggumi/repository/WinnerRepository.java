package com.uplus.ggumi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uplus.ggumi.domain.winner.Winner;

@Repository
public interface WinnerRepository extends JpaRepository<Winner, Long> {
}
