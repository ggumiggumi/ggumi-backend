package com.uplus.ggumi.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uplus.ggumi.domain.book.Book;
import com.uplus.ggumi.dto.book.BookResponseDto;
import com.uplus.ggumi.repository.BookRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class BookService {

	private final BookRepository bookRepository;

	public BookResponseDto search(String keyword) {
		List<Book> bookList = bookRepository.findByTitleContainingOrderByCreatedAt(keyword);
		List<BookResponseDto.SearchResult> searchResultList = bookList.stream()
			.map(book -> BookResponseDto.SearchResult.builder()
				.bookImage(book.getBook_image())
				.title(book.getTitle())
				.author(book.getAuthor())
				.publisher(book.getPublisher())
				.createdAt(book.getCreatedAt())
				.build())
			.collect(Collectors.toList());
		return BookResponseDto.builder().totalResultCount(bookList.size()).searchResultList(searchResultList).build();
	}
}
