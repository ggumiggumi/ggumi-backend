package com.uplus.ggumi.dto.child;

import com.uplus.ggumi.domain.child.Gender;
import com.uplus.ggumi.domain.parent.Parent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChildProfileResponseDto {

    private Long id;
    private String name;
    private LocalDate birthday;
    private int profileCode;
    private Gender gender;
    private Long parentId;
}
