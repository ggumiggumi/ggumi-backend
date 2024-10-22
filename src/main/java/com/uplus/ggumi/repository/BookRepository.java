package com.uplus.ggumi.repository;

import com.uplus.ggumi.domain.book.Book;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Book findBookById(Long bookId);

    @Modifying
    @Transactional
    @Query("UPDATE Book b SET b.likes = :likes WHERE b.id = :bookId")
    void updateLikes(Long bookId, int likes);
}
