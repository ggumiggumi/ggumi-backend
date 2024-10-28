package com.uplus.ggumi.dto.apply;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyRequestDto implements Serializable {

    private String name;
    private String phoneNumber;
    private Long applyTime;

}
