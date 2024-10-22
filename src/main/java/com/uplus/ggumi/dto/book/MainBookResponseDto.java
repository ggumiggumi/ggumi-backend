package com.uplus.ggumi.dto.book;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MainBookResponseDto {

    List<BookDto> books;
    private int number;
    private int size;

    @Getter
    @Builder
    public static class BookDto {

        private Long id;
        private String title;
        private String book_image;

    }
}
