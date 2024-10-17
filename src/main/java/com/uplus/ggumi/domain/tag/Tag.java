package com.uplus.ggumi.domain.tag;

import java.util.ArrayList;
import java.util.List;

import com.uplus.ggumi.domain.book_tag.BookTag;
import com.uplus.ggumi.domain.history.History;

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
public class Tag {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private double EI;
	private double SN;
	private double FT;
	private double PJ;

	@Builder.Default
	@OneToMany(mappedBy = "tag")
	private List<BookTag> bookTagList = new ArrayList<>();

}
