package com.uplus.ggumi.repository;

import java.util.List;

import com.uplus.ggumi.domain.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Book findBookById(Long bookId);

    List<Book> findByTitleContainingOrderByCreatedAt(String keyword);
}
