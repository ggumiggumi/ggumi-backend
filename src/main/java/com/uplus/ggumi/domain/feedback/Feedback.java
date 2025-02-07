package com.uplus.ggumi.domain.feedback;

import com.uplus.ggumi.domain.book.Book;
import com.uplus.ggumi.domain.child.Child;
import com.uplus.ggumi.domain.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Thumbs thumbs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id")
    private Child child;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    private LocalDateTime deletedAt;
    private boolean isDeleted;

    @Builder
    private Feedback(Child child, Book book) {
        this.child = child;
        this.book = book;
        this.thumbs = Thumbs.UNCHECKED;
    }

    /* 피드백 상태 변경을 위한 메서드 */
    public void updateThumbs(Thumbs newThumbs) {
        if (this.thumbs == newThumbs) {
            throw new IllegalStateException("같은 상태로 피드백을 업데이트 할 수 없습니다.");
        }
        this.thumbs = newThumbs;
    }

    public boolean isLikedBy(Child child) {
        return this.child.equals(child) && this.thumbs == Thumbs.UP;
    }
}
