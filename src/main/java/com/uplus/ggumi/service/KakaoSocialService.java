package com.uplus.ggumi.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.uplus.ggumi.config.exception.ApiException;
import com.uplus.ggumi.config.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class KakaoSocialService {

	private static final String KAKAO_TOKEN_INFO_URL = "https://kapi.kakao.com/v2/user/me";

	public HashMap<String, Object> getKakaoUserInfo(String kakaoAccessToken) {
		try {
			HttpURLConnection kakaoServerConnection = getKakaoServerConnectionForTokenInfo(kakaoAccessToken);
			if (kakaoServerConnection.getResponseCode() == 200) {
				return getKakaoUserInfoResponse(kakaoServerConnection);
			}
			throw new ApiException(ErrorCode.RESPONSE_CODE_ERROR);
		} catch (IOException e) {
			throw new ApiException(ErrorCode.FAILED_TO_RETRIEVE_KAKAO_USER_INFO);
		}
	}

	private static HttpURLConnection getKakaoServerConnectionForTokenInfo(String kakaoAccessToken) throws IOException {
		URL url = new URL(KAKAO_TOKEN_INFO_URL);
		HttpURLConnection kakaoServerConnection = (HttpURLConnection)url.openConnection();
		kakaoServerConnection.setRequestMethod("GET");
		kakaoServerConnection.setRequestProperty("Authorization", "Bearer " + kakaoAccessToken);
		// kakaoServerConnection.setDoOutput(true);
		//
		// try (BufferedWriter bw = new BufferedWriter(
		// 	new OutputStreamWriter(kakaoServerConnection.getOutputStream(), "UTF-8"))) {
		// 	String requestBody = "id_token=" + kakaoAccessToken;
		// 	bw.write(requestBody);
		// 	bw.flush();
		// }
		return kakaoServerConnection;
	}

	private HashMap<String, Object> getKakaoUserInfoResponse(HttpURLConnection kakaoServerConnection) throws IOException {
		String responseBody = readResponse(kakaoServerConnection);
		log.info("ResponseBody: {}", responseBody);

		JsonElement jsonElement = JsonParser.parseString(responseBody);
		String email = jsonElement.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
		String nickname = jsonElement.getAsJsonObject().get("properties").getAsJsonObject().get("nickname").getAsString();

		HashMap<String, Object> kakaoUserInfo = new HashMap<>();
		kakaoUserInfo.put("email", email);
		kakaoUserInfo.put("nickname", nickname);
		return kakaoUserInfo;
	}

	private String readResponse(HttpURLConnection kakaoServerConnection) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(kakaoServerConnection.getInputStream()));
		String responseBodyInput = "";
		StringBuilder responseBody = new StringBuilder();
		while ((responseBodyInput = br.readLine()) != null) {
			responseBody.append(responseBodyInput);
		}
		br.close();
		return responseBody.toString();
	}

}

