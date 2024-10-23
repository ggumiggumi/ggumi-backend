package com.uplus.ggumi.service;

import com.uplus.ggumi.domain.book.Book;
import com.uplus.ggumi.dto.book.BookResponseDto;
import com.uplus.ggumi.dto.book.MainBookResponseDto;
import com.uplus.ggumi.dto.book.MainBookResponseDto.BookDto;
import com.uplus.ggumi.repository.BookRepository;
import com.uplus.ggumi.repository.HistoryRepository;
import com.uplus.ggumi.repository.RecommendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class BookService {

    public static final int PAGE_SIZE = 4;

    private final BookRepository bookRepository;
    private final RecommendRepository recommendRepository;
    private final HistoryRepository historyRepository;

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

    /* 메인 페이지에서 MBTI 검사 여부에 따른 추천 도서 또는 최근 도서 페이징 조회 */
    public MainBookResponseDto getBooks(Long childId, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);

        boolean hasHistory = historyRepository.existsByChildId(childId);

        Page<BookDto> bookPage;
        if (hasHistory) {
            bookPage = recommendRepository.findByChildId(childId, pageable)
                    .map(recommend -> BookDto.builder()
                            .id(recommend.getBook().getId())
                            .title(recommend.getBook().getTitle())
                            .book_image(recommend.getBook().getBook_image())
                            .build());
        } else {
            bookPage = bookRepository.findLatestBooks(pageable)
                    .map(book -> BookDto.builder()
                            .id(book.getId())
                            .title(book.getTitle())
                            .book_image(book.getBook_image())
                            .build());
        }

        return MainBookResponseDto.builder()
                .books(bookPage.getContent())
                .number(bookPage.getNumber())
                .size(bookPage.getSize())
                .build();
    }

    /* 좋아요 수 기준으로 정렬된 리스트 가져오는 메서드*/
    public MainBookResponseDto getPopularBooks(int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);

        Page<BookDto> bookPage;

        bookPage = bookRepository.findAllByOrderByLikesDesc(pageable)
                .map(book-> BookDto.builder()
                        .id(book.getId())
                        .title(book.getTitle())
                        .book_image(book.getBook_image())
                        .build());

        return MainBookResponseDto.builder()
                .books(bookPage.getContent())
                .number(bookPage.getNumber())
                .size(bookPage.getSize())
                .build();
    }
}
