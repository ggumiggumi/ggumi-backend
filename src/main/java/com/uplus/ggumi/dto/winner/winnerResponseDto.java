package com.uplus.ggumi.dto.winner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class winnerResponseDto {
	private String name; // 홍0동
	private String phoneNumber; // 뒷번호 4자리
}
