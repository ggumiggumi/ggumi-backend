package com.uplus.ggumi.dto.apply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyRequestDto {

    private String name;
    private String phoneNumber;
    private Long applyTime;

}
