package com.uplus.ggumi.domain.feedback;

import com.uplus.ggumi.domain.book.Book;
import com.uplus.ggumi.domain.child.Child;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Feedback {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private Thumbs thumbs;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "child_id")
	private Child child;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id")
	private Book book;

}
