package com.uplus.ggumi.dto.apply;

import lombok.*;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyRequestDto {

    private String name;
    private String phoneNumber;

}
