package com.uplus.ggumi.domain.book;

import java.util.ArrayList;
import java.util.List;

import com.uplus.ggumi.domain.book_tag.BookTag;
import com.uplus.ggumi.domain.feedback.Feedback;
import com.uplus.ggumi.domain.global.BaseTimeEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
	private String title;
	private String author;
	private String publisher;
	private int recommend_age;

	@Builder.Default
	@OneToMany(mappedBy = "book")
	private List<Feedback> feedbackList = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "book")
	private List<BookTag> bookTagList = new ArrayList<>();

}
