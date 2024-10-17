package com.uplus.ggumi.repository;


import com.uplus.ggumi.domain.child.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChildRepository extends JpaRepository<Child, Long> {
    List<Child> findByParentId(Long parentId);
}
