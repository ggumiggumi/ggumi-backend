package com.uplus.ggumi.dto.history;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryRequestDto {
	@JsonProperty("EI")
	private double EI;
	@JsonProperty("FT")
	private double FT;
	@JsonProperty("PJ")
	private double PJ;
	@JsonProperty("SN")
	private double SN;
}
