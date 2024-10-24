package com.uplus.ggumi.domain.book;

import java.util.ArrayList;
import java.util.List;

import com.uplus.ggumi.domain.book_tag.BookTag;
import com.uplus.ggumi.domain.feedback.Feedback;
import com.uplus.ggumi.domain.global.BaseTimeEntity;
import com.uplus.ggumi.dto.book.BookManagementRequestDto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Book extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private double EI;
	private double SN;
	private double FT;
	private double PJ;

	@Lob
	private String content;

	private String book_image;
	private String title;
	private String author;
	private String publisher;
	private int likes;
	private int recommend_age;

	@Builder.Default
	@OneToMany(mappedBy = "book")
	private List<Feedback> feedbackList = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "book")
	private List<BookTag> bookTagList = new ArrayList<>();

	public void update(BookManagementRequestDto requestDto, String book_image_url) {

		this.EI = requestDto.getEI();
		this.SN = requestDto.getSN();
		this.FT = requestDto.getFT();
		this.PJ = requestDto.getPJ();

		this.content = requestDto.getContent();

		this.title = requestDto.getTitle();
		this.author = requestDto.getAuthor();
		this.publisher = requestDto.getPublisher();
		this.recommend_age = requestDto.getRecommend_age();

		this.book_image = book_image_url;
	}

}
