package com.uplus.ggumi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.uplus.ggumi.domain.parent.Parent;
import com.uplus.ggumi.domain.parent.Provider;

@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {

	@Query("select case when count(a) = 0 then true else false end from Parent a where a.email = :email and a.provider = :provider")
	boolean notExistsAccountByEmailAndProvider(String email, Provider provider);

	Optional<Parent> findByEmailAndProvider(String email, Provider provider);
}
