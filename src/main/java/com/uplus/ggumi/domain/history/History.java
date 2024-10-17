package com.uplus.ggumi.domain.history;

import java.time.LocalDateTime;

import com.uplus.ggumi.domain.book.Book;
import com.uplus.ggumi.domain.child.Child;
import com.uplus.ggumi.domain.global.BaseTimeEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class History extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private double EI;
	private double SN;
	private double FT;
	private double PJ;

	private LocalDateTime deletedAt;
	private boolean isDeleted;

	@ManyToOne
	@JoinColumn(name = "child_id")
	private Child child;

}