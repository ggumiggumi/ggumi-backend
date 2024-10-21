package com.uplus.ggumi.dto.bookDetail;

import com.uplus.ggumi.domain.feedback.Thumbs;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDetailResponseDto {

    private String title;
    private String author;
    private String publisher;
    private String bookImage;
    private String content;
    private LocalDateTime createdAt;
    private Thumbs feedback;

}
