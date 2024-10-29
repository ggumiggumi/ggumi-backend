package com.uplus.ggumi.dto.history;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyChildMBTITypeDto {
	private String name;
	private int profileCode;
	private double E;
	private double I;
	private double S;
	private double N;
	private double F;
	private double T;
	private double P;
	private double J;
	private MBTIType mbtiType;
	private String mbtiDesc;
	private String[] mbtiTags;
}
