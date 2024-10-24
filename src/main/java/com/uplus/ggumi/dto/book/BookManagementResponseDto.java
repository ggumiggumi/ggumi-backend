package com.uplus.ggumi.dto.book;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookManagementResponseDto {

	List<BookManagementResponseDto.BookDto> books;
	private int number;
	private int size;
	private int totalPages;

	@Getter
	@Builder
	public static class BookDto {
		private Long id;            // 책 아이디
		private String title;       // 책 제목

		private String author;      // 작가
		private String publisher;   // 출판사
		private int recommend_age;  // 권장 연령

		@JsonProperty("EI")
		private double EI;           // EI 강도

		@JsonProperty("SN")
		private double SN;           // SN 강도

		@JsonProperty("FT")
		private double FT;           // FT 강도

		@JsonProperty("PJ")
		private double PJ;           // PJ 강도

		private String content;      // 줄거리
		private String book_image;   // 책 이미지 경로
	}

}
