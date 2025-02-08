package com.uplus.ggumi.service;

import com.uplus.ggumi.domain.book.Book;
import com.uplus.ggumi.domain.book.MbtiScore;
import com.uplus.ggumi.domain.child.Child;
import com.uplus.ggumi.domain.child.Gender;
import com.uplus.ggumi.domain.feedback.Feedback;
import com.uplus.ggumi.domain.parent.Parent;
import com.uplus.ggumi.domain.parent.Provider;
import com.uplus.ggumi.domain.parent.Role;
import com.uplus.ggumi.repository.BookRepository;
import com.uplus.ggumi.repository.ChildRepository;
import com.uplus.ggumi.repository.FeedbackRepository;
import com.uplus.ggumi.repository.ParentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@SpringBootTest
@Transactional
public abstract class CreateDomain {

    @Autowired
    protected BookRepository bookRepository;

    @Autowired
    protected ChildRepository childRepository;

    @Autowired
    protected FeedbackRepository feedbackRepository;

    @Autowired
    private ParentRepository parentRepository;

    protected Book createBook(String title) {
        return bookRepository.save(Book.builder()
                .title(title)
                .author("Test Author")
                .publisher("Test Publisher")
                .mbtiScore(MbtiScore.builder()
                        .EI(0.5)
                        .SN(0.5)
                        .FT(0.5)
                        .PJ(0.5)
                        .build())
                .content("Test Content")
                .recommend_age(10)
                .book_image("Test image")
                .build());
    }

    protected Child createChild(String name, Parent parent) {
        return childRepository.save(Child.builder()
                .name(name)
                .parent(parent)
                .birthday(LocalDate.now())
                .gender(Gender.MALE)
                .build());
    }

    protected Parent createParent(String name) {
        return parentRepository.save(Parent.builder()
                .nickname(name)
                .email("test@email.com")
                .provider(Provider.KAKAO)
                .role(Role.USER)
                .build());
    }

    protected Feedback createFeedback(Child child, Book book) {
        return feedbackRepository.save(Feedback.builder()
                .book(book)
                .child(child)
                .build());
    }

}