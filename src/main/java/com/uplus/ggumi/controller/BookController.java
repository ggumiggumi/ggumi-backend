package com.uplus.ggumi.controller;

import com.uplus.ggumi.dto.book.MBTIResponseDto;
import com.uplus.ggumi.service.OpenAIService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.uplus.ggumi.config.response.ResponseDto;
import com.uplus.ggumi.config.response.ResponseUtil;
import com.uplus.ggumi.dto.book.BookManagementRequestDto;
import com.uplus.ggumi.dto.book.BookManagementResponseDto;
import com.uplus.ggumi.dto.book.BookResponseDto;
import com.uplus.ggumi.service.BookService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Tag(name = "도서")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books")
public class BookController {

	private final BookService bookService;
	private final OpenAIService openAIService;

	@Operation(summary = "제목으로 도서 검색")
	@GetMapping("/search")
	public ResponseDto<BookResponseDto> searchBooks(@RequestParam String keyword) {
		return ResponseUtil.SUCCESS(keyword + " 검색에 성공하였습니다.", bookService.search(keyword));
	}

	@Operation(summary = "도서 정보 등록")
	@PostMapping("")
	public ResponseDto<Long> createBook(
			@RequestBody BookManagementRequestDto requestDto) throws Exception {
		// GPT를 호출하여 MBTI 값 생성
		String concatTitleAndContent = "책제목" + requestDto.getTitle() + "책 내용" + requestDto.getContent() + "제목이 더 중요하고 이 책의 MBTI필요해";
		if (concatTitleAndContent.length() > 200) {

			concatTitleAndContent = concatTitleAndContent.substring(0, 200);
		}
		String gptResponse = openAIService.getChatGPTResponse(concatTitleAndContent); // 제목을 기반으로 MBTI 값 요청

		requestDto = extractMBTIValues(gptResponse, requestDto);
		// MBTI 값을 requestDto에 설정
//		requestDto.setMbti(mbtiValue); // MBTI 값을 DTO에 추가하는 메서드 필요
		return ResponseUtil.SUCCESS("도서 정보 등록에 성공하였습니다.", bookService.createBook(requestDto));
	}

	@Operation(summary = "도서 정보 수정")
	@PutMapping("/{bookId}")
	public ResponseDto<Long> updateBook(
			@PathVariable Long bookId, @RequestPart BookManagementRequestDto requestDto,
			@RequestPart MultipartFile imageFile) {
		return ResponseUtil.SUCCESS("도서 정보 수정에 성공하였습니다.", bookService.updateBook(bookId, requestDto, imageFile));
	}

	@Operation(summary = "도서 정보 삭제")
	@DeleteMapping("/{bookId}")
	public ResponseDto<Long> deleteBook(@PathVariable Long bookId) {
		return ResponseUtil.SUCCESS("도서 정보 삭제에 성공하였습니다.", bookService.deleteBook(bookId));
	}

	/*@Operation(summary = "도서 정보 목록 가져오기")
	@GetMapping("/list")
	public ResponseDto<BookManagementResponseDto> getBookList(@RequestParam int page) {
		return ResponseUtil.SUCCESS("도서 정보 리스트를 가져오는 것을 성공하였습니다.",
			bookService.getBookList());  // 반환 타입이 List가 아니라 BookManagementResponseDto
	}*/

	@Operation(summary = "특정 도서 정보 가져오기")
	@GetMapping("/{bookId}")
	public ResponseDto<BookManagementResponseDto.BookDto> getBook(@PathVariable Long bookId) {
		return ResponseUtil.SUCCESS("도서 정보를 가져오는 것을 성공하였습니다.", bookService.getBook(bookId));
	}

	@Operation(summary = "도서 정보 목록 가져오기")
	@GetMapping("/list")
	public ResponseDto<BookManagementResponseDto> getBookList(@RequestParam int page) {
		return ResponseUtil.SUCCESS("도서 정보를 가져오는 것을 성공하였습니다.", bookService.getBookList(page));
	}

	// MBTI 값을 추출하는 메서드
	private BookManagementRequestDto extractMBTIValues(String gptResponse, BookManagementRequestDto requestDto) {
		// 정규 표현식 패턴 정의
		Pattern pattern = Pattern.compile("\\((\\w):\\s*(\\d+),\\s*(\\w):\\s*(\\d+),\\s*(\\w):\\s*(\\d+),\\s*(\\w):\\s*(\\d+)\\)");
		Matcher matcher = pattern.matcher(gptResponse);

		if (matcher.find()) {
			String[] types = {matcher.group(1), matcher.group(3), matcher.group(5), matcher.group(7)};
			Integer[] values = {
					Integer.parseInt(matcher.group(2)),
					Integer.parseInt(matcher.group(4)),
					Integer.parseInt(matcher.group(6)),
					Integer.parseInt(matcher.group(8))
			};

			// 각 MBTI 유형에 대한 값 설정
			for (int i = 0; i < types.length; i++) {
				String type = types[i];
				int score = values[i];

				// I, S, F, P를 각각 E, N, T, J로 변환 가능
				if (type.equals("I")) type = "E"; // I가 E로 변경될 수 있음
				else if (type.equals("S")) type = "N"; // S가 N으로 변경될 수 있음
				else if (type.equals("F")) type = "T"; // F가 T로 변경될 수 있음
				else if (type.equals("P")) type = "J"; // P가 J로 변경될 수 있음

				// requestDto에 MBTI 값을 설정
				switch (type) {
					case "E":
						requestDto.updateEI(score);
						break;
					case "N":
						requestDto.updateSN(score);
						break;
					case "T":
						requestDto.updateFT(score);
						break;
					case "J":
						requestDto.updatePJ(score);
						break;
				}
			}
		}
		return requestDto;
	}

}
