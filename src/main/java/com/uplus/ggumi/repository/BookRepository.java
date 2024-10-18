package com.uplus.ggumi.repository;

import com.uplus.ggumi.domain.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    Book findBookById(Long bookId);
}
