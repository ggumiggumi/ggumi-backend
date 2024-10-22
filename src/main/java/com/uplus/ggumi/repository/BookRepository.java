package com.uplus.ggumi.repository;

import java.util.List;

import com.uplus.ggumi.domain.book.Book;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Book findBookById(Long bookId);

    List<Book> findByTitleContainingOrderByCreatedAt(String keyword);

    @Modifying
    @Transactional
    @Query("UPDATE Book b SET b.likes = :likes WHERE b.id = :bookId")
    void updateLikes(Long bookId, int likes);

    @Query("SELECT b FROM Book b ORDER BY b.createdAt DESC ")
    Page<Book> findLatestBooks(Pageable pageable);
}
