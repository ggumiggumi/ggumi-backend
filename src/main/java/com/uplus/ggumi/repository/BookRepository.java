package com.uplus.ggumi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.uplus.ggumi.domain.book.Book;

import jakarta.transaction.Transactional;

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

	Page<Book> findAllByOrderByLikesDesc(Pageable pageable);

	@Query("SELECT b FROM Book b WHERE " +
		"(b.EI BETWEEN :eiMin AND :eiMax) AND " +
		"(b.FT BETWEEN :ftMin AND :ftMax) AND " +
		"(b.PJ BETWEEN :pjMin AND :pjMax) AND " +
		"(b.SN BETWEEN :snMin AND :snMax)")
	List<Book> findBooksByMultipleRanges(
		@Param("eiMin") double eiMin, @Param("eiMax") double eiMax,
		@Param("ftMin") double ftMin, @Param("ftMax") double ftMax,
		@Param("pjMin") double pjMin, @Param("pjMax") double pjMax,
		@Param("snMin") double snMin, @Param("snMax") double snMax);

	@Query("SELECT b FROM Book b WHERE " +
		"((b.EI >= 0.5 AND :childEI >= 0.5) OR (b.EI < 0.5 AND :childEI < 0.5)) " +
		"AND ((b.FT >= 0.5 AND :childFT >= 0.5) OR (b.FT < 0.5 AND :childFT < 0.5)) " +
		"AND ((b.PJ >= 0.5 AND :childPJ >= 0.5) OR (b.PJ < 0.5 AND :childPJ < 0.5)) " +
		"AND ((b.SN >= 0.5 AND :childSN >= 0.5) OR (b.SN < 0.5 AND :childSN < 0.5))")
	List<Book> findBooksByMultipleAttributes(
		@Param("childEI") double childEI,
		@Param("childFT") double childFT,
		@Param("childPJ") double childPJ,
		@Param("childSN") double childSN);

}
