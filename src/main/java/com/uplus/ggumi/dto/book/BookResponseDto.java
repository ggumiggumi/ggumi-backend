package com.uplus.ggumi.dto.book;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookResponseDto {

	private int totalResultCount;
	private List<SearchResult> searchResultList;

	@Getter
	@Builder
	public static class SearchResult {
		@Schema(name = "표지 이미지")
		private String bookImage;
		private String title;
		@Schema(name = "저자")
		private String author;
		@Schema(name = "출판사")
		private String publisher;
		private LocalDateTime createdAt;
	}

}
