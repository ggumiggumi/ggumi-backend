package com.uplus.ggumi.dto.book;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class MBTIResponseDto {

    private double EI;
    private double SN;
    private double FT;
    private double PJ;

}
