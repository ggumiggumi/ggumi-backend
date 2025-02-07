package com.uplus.ggumi.repository;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.uplus.ggumi.domain.book.Book;

import jakarta.transaction.Transactional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

	Optional<Book> findById(Long bookId);

	List<Book> findByTitleContainingOrderByCreatedAt(String keyword);

	@Modifying
	@Transactional
	@Query("UPDATE Book b SET b.likes = :likes WHERE b.id = :bookId")
	void updateLikes(Long bookId, int likes);

	@Query("SELECT b FROM Book b ORDER BY b.createdAt DESC ")
	Page<Book> findLatestBooks(Pageable pageable);

	Page<Book> findAllByOrderByLikesDesc(Pageable pageable);

	// 좋아요 많은순, 최신 순으로 (limit)개의 도서 정보를 가져오는 메서드
	@Query(value = "SELECT * FROM book ORDER BY likes DESC, id DESC LIMIT :limit", nativeQuery = true)
	List<Book> findTopPopularBooks(@Param("limit") int limit);

	List<Book> findAll();

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT b FROM Book b WHERE b.id = :id")
	Optional<Book> findByIdWithPessimisticLock(@Param("id") Long id);
}
