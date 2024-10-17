package com.uplus.ggumi.domain.child;

import com.uplus.ggumi.domain.feedback.Feedback;
import com.uplus.ggumi.domain.global.BaseTimeEntity;
import com.uplus.ggumi.domain.history.History;
import com.uplus.ggumi.domain.parent.Parent;
import com.uplus.ggumi.dto.child.ChildProfileRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Child extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDate birthday;

    private int profileCode;

    @Enumerated(value = EnumType.STRING)
    private Gender gender;

    @ManyToOne
    @JoinColumn(name="parent_id")
    private Parent parent;

    @Builder.Default
    @OneToMany(mappedBy = "child")
    private List<Feedback> feedbackList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "child")
    private List<History> historyList = new ArrayList<>();

    public void update(ChildProfileRequestDto requestDto) {
        this.name = requestDto.getName();
        this.birthday = requestDto.getBirthday();
        this.profileCode = requestDto.getProfileCode();
        this.gender = requestDto.getGender();
    }
}
