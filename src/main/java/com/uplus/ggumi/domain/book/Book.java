package com.uplus.ggumi.domain.book;

import com.uplus.ggumi.domain.book_tag.BookTag;
import com.uplus.ggumi.domain.child.Child;
import com.uplus.ggumi.domain.feedback.Feedback;
import com.uplus.ggumi.domain.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Entity
@NoArgsConstructor
public class Book extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false)
    private String content;

    private String book_image;

    @Column(nullable = false)
    private String title;

    @Embedded
    private MbtiScore mbtiScore;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String publisher;

    @Column(nullable = false)
    private int likes;

    private int recommend_age;

    @OneToMany(mappedBy = "book")
    private Set<Feedback> feedbackList = new HashSet<>();

    @OneToMany(mappedBy = "book")
    private List<BookTag> bookTagList = new ArrayList<>();

    @Builder
    private Book(String title, String content, String author, String publisher, int recommend_age, MbtiScore mbtiScore, String book_image) {
        this.title = title;
        this.author = author;
        this.content = content;
        this.publisher = publisher;
        this.likes = 0;
        this.recommend_age = recommend_age;
        this.mbtiScore = mbtiScore;
        this.book_image = book_image;
    }

    public void incrementLikes() {
        this.likes++;
    }

    public void decrementLikes() {
        if (this.likes > 0) {
            this.likes--;
        }
    }

    public boolean hasBeenLikedBy(Child child) {
        return feedbackList.stream()
                .anyMatch(feedback -> feedback.isLikedBy(child));
    }
}

