package com.uplus.ggumi.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.uplus.ggumi.config.exception.ApiException;
import com.uplus.ggumi.config.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class S3Service {

	private final AmazonS3Client s3Client;

	@Value("${cloud.aws.S3.bucket}")
	private String bucketName;

	/** 이미지 업로드 **/
	public String uploadFile(MultipartFile image) {

		try {
			/* 업로드할 파일의 이름을 변경 */
			String fileName = createFileName(image.getOriginalFilename());

			/* S3에 업로드할 파일의 메타데이터 생성 */
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType(image.getContentType());
			metadata.setContentLength(image.getSize());

			/* S3에 파일 업로드 */
			s3Client.putObject(bucketName, fileName, image.getInputStream(), metadata);

			/* 업로드한 파일의 S3 URL 주소 반환 */
			return s3Client.getUrl(bucketName, fileName).toString();

		} catch (IOException e) {
			throw new ApiException(ErrorCode.S3_UPLOAD_FAILED);
		}

	}

	/** 이미지 파일 갱신 **/
	public String updateFile(String imageFileUrl, MultipartFile newImage) {
		try {

			// 파일명 찾기
			String fileName = getFileNameFromUrl(imageFileUrl);

			// 새 이미지의 메타데이터 생성
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType(newImage.getContentType());
			metadata.setContentLength(newImage.getSize());

			// S3에 새 파일 업로드 (기존 파일명으로 덮어쓰기)
			s3Client.putObject(bucketName, fileName, newImage.getInputStream(), metadata);

			// 업로드한 파일의 S3 URL 주소 반환
			return s3Client.getUrl(bucketName, fileName).toString();

		} catch (IOException e) {
			throw new ApiException(ErrorCode.S3_UPDATE_FAILED);
		}
	}

	/** 이미지 파일명 생성 **/
	private String createFileName(String originalFilename) {

		// 원본 파일 확장자 구하기
		String fileExtension = ""; // 예: ".jpg"
		if (originalFilename != null && originalFilename.contains(".")) {
			fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
		}

		// 업로드할 파일 이름 설정 및 반환
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		return LocalDateTime.now().format(formatter) + "-book-image" + fileExtension;
	}

	/** 이미지 파일 삭제 **/
	public void deleteFile(String imageFileUrl) {
		// S3에서 삭제할 파일의 이름 추출
		String fileName = getFileNameFromUrl(imageFileUrl);

		try {
			// S3에서 파일 삭제
			s3Client.deleteObject(bucketName, fileName);
		} catch (Exception e) {
			throw new ApiException(ErrorCode.S3_DELETE_FAILED);
		}
	}

	/** URL에서 파일 이름 추출 **/
	private String getFileNameFromUrl(String fileUrl) {
		return fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
	}

}
