package com.uplus.ggumi.dto.history;

import com.uplus.ggumi.domain.history.History;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MbtiHistoryPageDto {

    private String name;
    private int profileCode;

    private List<HistoryDtoForHistoryPage> histories;
}
