package com.uplus.ggumi.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.uplus.ggumi.config.exception.ApiException;
import com.uplus.ggumi.config.exception.ErrorCode;
import com.uplus.ggumi.domain.book.Book;
import com.uplus.ggumi.dto.book.BookManagementRequestDto;
import com.uplus.ggumi.dto.book.BookManagementResponseDto;
import com.uplus.ggumi.dto.book.BookResponseDto;
import com.uplus.ggumi.dto.book.MainBookResponseDto;
import com.uplus.ggumi.dto.book.MainBookResponseDto.BookDto;
import com.uplus.ggumi.repository.BookRepository;
import com.uplus.ggumi.repository.HistoryRepository;
import com.uplus.ggumi.repository.RecommendRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class BookService {

	public static final int PAGE_SIZE = 4;
	public static final int BOOK_MANAGEMENT_PAGE_SIZE = 10;

	private final BookRepository bookRepository;
	private final RecommendRepository recommendRepository;
	private final HistoryRepository historyRepository;

	private final S3Service s3Service;

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
			.totalPages(bookPage.getTotalPages())
			.build();
	}

	/* 좋아요 수 기준으로 정렬된 리스트 가져오는 메서드*/
	public MainBookResponseDto getPopularBooks(int page) {
		Pageable pageable = PageRequest.of(page, PAGE_SIZE);

		Page<BookDto> bookPage;

		bookPage = bookRepository.findAllByOrderByLikesDesc(pageable)
			.map(book -> BookDto.builder()
				.id(book.getId())
				.title(book.getTitle())
				.book_image(book.getBook_image())
				.build());

		return MainBookResponseDto.builder()
			.books(bookPage.getContent())
			.number(bookPage.getNumber())
			.size(bookPage.getSize())
			.totalPages(bookPage.getTotalPages())
			.build();
	}

	/** 도서 정보 등록 **/
	public Long createBook(BookManagementRequestDto requestDto, MultipartFile bookImage) {

		// S3에 이미지 업로드 (이미지 URL 생성)
		String bookImageUrl = s3Service.uploadFile(bookImage);

		// Book 엔티티 생성
		Book book = Book.builder()
			.title(requestDto.getTitle())
			.author(requestDto.getAuthor())
			.publisher(requestDto.getPublisher())
			.recommend_age(requestDto.getRecommend_age())
			.EI(requestDto.getEI())
			.SN(requestDto.getSN())
			.FT(requestDto.getFT())
			.PJ(requestDto.getPJ())
			.content(requestDto.getContent())
			.book_image(bookImageUrl) // S3에서 생성된 이미지 URL
			.build();

		return bookRepository.save(book).getId();
	}

	/** 도서 정보 수정 **/
	public Long updateBook(Long bookId, BookManagementRequestDto requestDto, MultipartFile bookImage) {

		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new ApiException(ErrorCode.BOOK_NOT_EXIST));

		// S3 책 이미지 갱신하기
		String bookImageUrl = s3Service.updateFile(book.getBook_image(), bookImage);

		book.update(requestDto, bookImageUrl);

		return book.getId();
	}

	/** 도서 정보 삭제 **/
	public Long deleteBook(Long bookId) {

		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new ApiException(ErrorCode.BOOK_NOT_EXIST));

		// S3 책 이미지 삭제하기
		s3Service.deleteFile(book.getBook_image());

		bookRepository.delete(book);

		return book.getId();
	}

	/** 특정 도서 정보 반환 **/
	public BookManagementResponseDto.BookDto getBook(Long bookId) {
		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new ApiException(ErrorCode.BOOK_NOT_EXIST));

		return BookManagementResponseDto.BookDto.builder()
			.id(book.getId())
			.title(book.getTitle())
			.author(book.getAuthor())
			.publisher(book.getPublisher())
			.recommend_age(book.getRecommend_age())
			.EI(book.getEI())
			.SN(book.getSN())
			.FT(book.getFT())
			.PJ(book.getPJ())
			.content(book.getContent())
			.book_image(book.getBook_image())
			.build();

	}

	public BookManagementResponseDto getBookList(int page) {

		Pageable pageable = PageRequest.of(page, BOOK_MANAGEMENT_PAGE_SIZE);

		Page<BookManagementResponseDto.BookDto> bookPage;

		bookPage = bookRepository.findAll(pageable)
			.map(book -> BookManagementResponseDto.BookDto.builder()
				.id(book.getId())
				.title(book.getTitle())
				.author(book.getAuthor())
				.publisher(book.getPublisher())
				.recommend_age(book.getRecommend_age())
				.EI(book.getEI())
				.SN(book.getSN())
				.FT(book.getFT())
				.PJ(book.getPJ())
				.content(book.getContent())
				.book_image(book.getBook_image())
				.build());

		return BookManagementResponseDto.builder()
			.books(bookPage.getContent())
			.number(bookPage.getNumber())
			.size(bookPage.getSize())
			.totalPages(bookPage.getTotalPages())
			.build();
	}

}
