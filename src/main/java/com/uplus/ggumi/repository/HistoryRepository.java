package com.uplus.ggumi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uplus.ggumi.domain.history.History;

import jdk.jfr.Registered;

@Registered
public interface HistoryRepository extends JpaRepository<History, Long> {
}
