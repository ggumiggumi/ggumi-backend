/*
package com.uplus.ggumi.batch.job;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.uplus.ggumi.domain.feedback.Feedback;
import com.uplus.ggumi.domain.history.History;
import com.uplus.ggumi.domain.recommend.Recommend;
import com.uplus.ggumi.repository.FeedbackRepository;
import com.uplus.ggumi.repository.HistoryRepository;
import com.uplus.ggumi.repository.RecommendRepository;

import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class HardDeletePersonalInfoJobConfig {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final EntityManagerFactory entityManagerFactory;
	private final FeedbackRepository feedbackRepository;
	private final HistoryRepository historyRepository;
	private final RecommendRepository recommendRepository;

	@Bean
	public Job hardDeletePersonalInfoJob() {
		return new JobBuilder("hardDeletePersonalInfoJob", jobRepository)
			.incrementer(new RunIdIncrementer())
			.start(deleteFeedbackStep())
			.next(deleteHistoryStep())
			.next(deleteRecommendStep())
			.build();
	}

	@Bean
	public Step deleteFeedbackStep() {
		return new StepBuilder("deleteFeedbackStep", jobRepository)
			.<Feedback, Feedback>chunk(10, transactionManager)
			.reader(feedbackReader())
			.processor(feedbackProcessor())
			.writer(feedbackWriter())
			.build();
	}

	@Bean
	public Step deleteHistoryStep() {
		return new StepBuilder("deleteHistoryStep", jobRepository)
			.<History, History>chunk(10, transactionManager)
			.reader(historyReader())
			.processor(historyProcessor())
			.writer(historyWriter())
			.build();
	}

	@Bean
	public Step deleteRecommendStep() {
		return new StepBuilder("deleteRecommendStep", jobRepository)
			.<Recommend, Recommend>chunk(10, transactionManager)
			.reader(recommendReader())
			.processor(recommendProcessor())
			.writer(recommendWriter())
			.build();
	}

	// Feedback Reader, Processor, and Writer
	@Bean
	public JpaPagingItemReader<Feedback> feedbackReader() {
		JpaPagingItemReader<Feedback> reader = new JpaPagingItemReader<>() {
			@Override
			public int getPage() {
				return 0;
			}
		};
		reader.setEntityManagerFactory(entityManagerFactory);
		reader.setQueryString("SELECT f FROM Feedback f WHERE f.isDeleted = true AND f.deletedAt < :date");
		reader.setParameterValues(Map.of("date", LocalDateTime.now().minusDays(30)));
		reader.setPageSize(10);
		return reader;
	}

	@Bean
	public ItemProcessor<Feedback, Feedback> feedbackProcessor() {
		return item -> {
			System.out.println("Deleting Feedback: " + item.getId()); // 데이터 로깅
			return item;
		};
	}

	@Bean
	public RepositoryItemWriter<Feedback> feedbackWriter() {
		return new RepositoryItemWriterBuilder<Feedback>()
			.repository(feedbackRepository)
			.methodName("delete")
			.build();
	}

	// History Reader, Processor, and Writer
	@Bean
	public JpaPagingItemReader<History> historyReader() {
		JpaPagingItemReader<History> reader = new JpaPagingItemReader<>() {
			@Override
			public int getPage() {
				return 0;
			}
		};
		reader.setEntityManagerFactory(entityManagerFactory);
		reader.setQueryString("SELECT h FROM History h WHERE h.isDeleted = true AND h.deletedAt < :date");
		reader.setParameterValues(Map.of("date", LocalDateTime.now().minusDays(30)));
		reader.setPageSize(10);
		return reader;
	}

	@Bean
	public ItemProcessor<History, History> historyProcessor() {
		return item -> {
			System.out.println("Deleting History: " + item.getId()); // 데이터 로깅
			return item;
		};
	}

	@Bean
	public RepositoryItemWriter<History> historyWriter() {
		return new RepositoryItemWriterBuilder<History>()
			.repository(historyRepository)
			.methodName("delete")
			.build();
	}

	// Recommend Reader, Processor, and Writer
	@Bean
	public JpaPagingItemReader<Recommend> recommendReader() {
		JpaPagingItemReader<Recommend> reader = new JpaPagingItemReader<>() {
			@Override
			public int getPage() {
				return 0;
			}
		};
		reader.setEntityManagerFactory(entityManagerFactory);
		reader.setQueryString("SELECT r FROM Recommend r WHERE r.isDeleted = true AND r.deletedAt < :date");
		reader.setParameterValues(Map.of("date", LocalDateTime.now().minusDays(30)));
		reader.setPageSize(10);
		return reader;
	}

	@Bean
	public ItemProcessor<Recommend, Recommend> recommendProcessor() {
		return item -> {
			System.out.println("Deleting Recommend: " + item.getId()); // 데이터 로깅
			return item;
		};
	}

	@Bean
	public RepositoryItemWriter<Recommend> recommendWriter() {
		return new RepositoryItemWriterBuilder<Recommend>()
			.repository(recommendRepository)
			.methodName("delete")
			.build();
	}
}
*/
