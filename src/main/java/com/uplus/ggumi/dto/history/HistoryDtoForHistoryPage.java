package com.uplus.ggumi.dto.history;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryDtoForHistoryPage {

    private double EI;
    private double SN;
    private double FT;
    private double PJ;

    private LocalDateTime createdAt;
}
