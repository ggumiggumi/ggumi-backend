package com.uplus.ggumi.config.S3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;

@Configuration
public class S3Config {

	@Value("${cloud.aws.credentials.access-key}")
	private String accessKey;

	@Value("${cloud.aws.credentials.secret-key}")
	private String accessSecret;

	@Value("${cloud.aws.region.static}")
	private String region;

	@Bean
	public AmazonS3Client s3Client() {
		BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, accessSecret);

		return (AmazonS3Client)AmazonS3Client.builder()
			.withRegion(region)
			.withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
			.build();
	}
}
