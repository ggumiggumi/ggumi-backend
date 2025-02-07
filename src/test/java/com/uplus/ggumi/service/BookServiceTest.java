package com.uplus.ggumi.service;

import com.uplus.ggumi.domain.book.Book;
import com.uplus.ggumi.domain.child.Child;
import com.uplus.ggumi.domain.feedback.Feedback;
import com.uplus.ggumi.domain.parent.Parent;
import com.uplus.ggumi.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
class BookServiceTest extends CreateDomain {

    @Autowired
    private BookDetailService bookDetailService;
    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("동시에 여러 사용자가 좋아요를 눌러도 좋아요 수가 정확히 증가해야 한다")
    void concurrentLikeTest() throws InterruptedException {
        // given
        Book book = createBook("Test Book");
        Parent parent = createParent("Test Parent");

        int numberOfThreads = 10;
        List<Child> children = new ArrayList<>();
        List<Feedback> feedbacks = new ArrayList<>();

        // 데이터 초기화 및 영속화
        for (int i = 0; i < numberOfThreads; i++) {
            Child child = createChild("Child" + i, parent);
            children.add(child);

            Feedback feedback = createFeedback(child, book);
            feedbacks.add(feedback);
        }

        // 모든 데이터가 DB에 반영되도록 명시적으로 플러시
        TestTransaction.flagForCommit();
        TestTransaction.end();

        // 새로운 트랜잭션 시작
        TestTransaction.start();

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // when
        for (int i = 0; i < numberOfThreads; i++) {
            Child child = children.get(i);
            executorService.submit(() -> {
                try {
                    bookDetailService.updateLike(book.getId(), child.getId());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        Book updatedBook = bookRepository.findById(book.getId()).orElseThrow();
        assertThat(updatedBook.getLikes()).isEqualTo(numberOfThreads);
    }

    /*@Test
    @DisplayName("동시에 좋아요/싫어요를 눌러도 정확한 좋아요 수가 유지되어야 한다")
    void concurrentLikeAndHateTest() throws InterruptedException {
        // given
        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        Book book = createBook();
        List<Child> children = createChild(numberOfThreads);

        // when
        for (int i = 0; i < numberOfThreads; i++) {
            Child child = children.get(i);
            final int index = i;
            executorService.submit(() -> {
                try {
                    if (index % 2 == 0) {
                        bookDetailService.updateLike(book.getId(), child.getId());
                    } else {
                        bookDetailService.updateHate(book.getId(), child.getId());
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        Book updatedBook = bookRepository.findById(book.getId()).orElseThrow();
        List<Feedback> feedbacks = feedbackRepository.findByBookId(book.getId());

        long likeCount = feedbacks.stream()
                .filter(f -> f.getThumbs() == Thumbs.UP)
                .count();

        assertThat(updatedBook.getLikes()).isEqualTo(likeCount);
    }*/

}
