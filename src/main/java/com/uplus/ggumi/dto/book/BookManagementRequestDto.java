package com.uplus.ggumi.dto.book;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookManagementRequestDto {

	private String title;        // 책 제목
	private String author;       // 작가
	private String publisher;    // 출판사
	private int recommend_age;  // 권장 연령

	@JsonProperty("EI")
	private double EI;         // EI 강도

	@JsonProperty("SN")
	private double SN;         // SN 강도

	@JsonProperty("FT")
	private double FT;         // FT 강도

	@JsonProperty("PJ")
	private double PJ;         // PJ 강도

	@Lob
	private String content;    // 줄거리


	public void updateEI(double EI) { // MBTI 값의 setter
		this.EI = EI;
	}
	public void updateSN(double SN) {
		this.SN = SN;
	}
	public void updateFT(double FT) {
		this.FT = FT;
	}
	public void updatePJ(double PJ) {
		this.PJ = PJ;
	}

}
