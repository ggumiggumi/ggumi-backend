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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Apply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "parent_id")
        private Parent parent;
    */
    private Long parentId;
    private Long applyTime;

}
