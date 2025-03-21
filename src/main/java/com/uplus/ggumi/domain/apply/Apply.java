package com.uplus.ggumi.domain.apply;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Apply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String phoneNumber;
    private Long applyTime;

    @Builder
    public Apply(String name, String phoneNumber, Long applyTime) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.applyTime = applyTime;
    }
}
