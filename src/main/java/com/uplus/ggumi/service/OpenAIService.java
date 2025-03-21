/*
package com.uplus.ggumi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
public class OpenAIService {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;
    private final String userPrompt1 = "책 제목 우렁각시 줄거리를 알려줄게이야기 제목: 우렁이 각시\n\n등장인물:\n- 농부: 혼자 농사를 지으며 살아가는 20세가 넘은 미혼의 남성\n- 우렁이 여인: 항아리 속 우렁이에서 나온 아름다운 여인, 농부를 위해 집안일을 도맡음\n\n줄거리:\n1. 농부의 탄식: 혼자 농사를 지으며 외로움을 느끼던 농부가 한숨을 지으며 혼잣말을 한다.\n2. 우렁이와의 만남: 농부가 한숨을 짓자 '나랑 같이 먹지'라는 소리가 들려와, 논두렁에 있는 우렁이를 발견하고 집에 데려온다.\n3. 기이한 현상: 다음 날부터 농부가 집에 돌아오면 마루에 밥상이 차려져 있고, 집안이 청소되어 있다.\n4. 몰래 지켜봄: 수상하게 여긴 농부가 몰래 지켜보다가 우렁이에서 나온 여인이 밥상을 차리고 집안 청소를 하는 것을 발견한다.\n5. 여인의 정체 확인: 농부가 꿈인지 현실인지 헷갈려 항아리를 확인하지만, 여인 대신 우렁이만 둥둥 떠 있다.\n\n요약: 농부는 외로움에 한탄하던 중 논두렁에서 우렁이를 발견하고 집에 데려와 물 항아리에 넣어둔다. 다음 날부터 이상하게 밥상이 차려져 있고 집안 청소가 되어 있어 이를 수상하게 여긴 농부는 몰래 지켜보다 우렁이에서 나온 여인이 일을 해주는 모습을 발견한다.";

    private final String systemPrompt1 = "Mayer's prediction 중 에너지 I냐 E,정보 S냐 N, 결정 F나 T, 생활 P냐 J 뭔지 결정해야 하는 상황이야. 40 60 20 80 같은 4차원 벡터 형식으로 처음에 적어줘";
    private final String systemPrompt2 = "명심해야 해 출력형식이 제일 중요해. Mayer's prediction 중 4가지 대문자에 대한 값을 0에서 100사이의 강도를 너의 답 맨 앞에 적어줘 50미만이면 E, S,F,P에 가깝고 50이상이면 I와 N 과 T와 J에 가까워" +
            "" +
            "이 형식으로 출력해줘 (I:60, S:80, T:60, P:40)";

    private final String apiUrl = "https://api.openai.com/v1/chat/completions";

    public String getChatGPTResponse(String userInput) throws Exception {  // 파라미터로 userInput 추가

        // JSON 요청 본문 생성
        JSONObject json = new JSONObject();
//        json.put("model", "gpt-4");
//
//        // 메시지 생성
//        JSONArray messages = new JSONArray();
//        messages.put(new JSONObject().put("role", "user").put("content", userInput)); // userInput 사용
//        messages.put(new JSONObject().put("role", "system").put("content", "책별로 MBTI를 매핑해야 해. MBTI강도는 0에서 100 사이의 값으로 해."));
        json.put("model", "gpt-4");

        JSONArray messages = new JSONArray();

        messages.put(new JSONObject()
                .put("role", "system")
                .put("content", "책별로 MBTI를 매핑해야 해 . MBTI강도는 0에서 100사이의 값으로 해"));

        messages.put(new JSONObject()
                .put("role", "system")
                .put("content", "MBTI  유형  책  제목 줄거리 작가 출판사 ...\\nISTJ 해리포터와 마법사의 돌 마법과 친구를 찾는 소년의 이야기. J.K. 롤링 문학동네...\\nISTP 자유의 날갯짓 비행기 조종사가 된 소년의 도전. 타카기 노리코 알에이치비...\\nISFP 우리의 소원 소원과 꿈을 중심으로 한 다양한 이야기 모음. 마르그리트 미셸 푸른하늘...\\nESTP 웃음의 힘 웃음과 유머가 삶에 미치는 영향. 쥐스탕 리사 현대 마음...\\nESFP 행복한 일상 일상에서 행복을 찾는 법에 대한 이야기. 아오바 겐 행복한 세상...\\nISFJ 꿈꾸는 아이들 아이들의 꿈과 가능성에 대한 이야기. 장 마르크 연두출판사...\\nISFP 그림으로 그린 꿈 다양한 꿈과 목표를 그림으로 표현한 책. 루시 그랩 비주얼북...\\nENFJ 사회적 책임 사회와 공동체에 대한 개인의 책임에 대한 탐구. 낸시 필립스 인간사회...\\nESTP 모험의 시작 주인공의 모험을 중심으로 한 이야기. 사라 루카스 주니어 라이브러리...\\nESFP 음악의 힘 음악이 사람들에게 미치는 영향과 이야기. 조지 빈센트 현대음악...\\nISFP 바다의 노래 바다의 아름다움과 그에 대한 이야기. 조지 아다무르 자연의 탐험...\\nINFP 너의 꿈을 꿉니다 꿈을 향한 여정을 다룬 소설. 노라 로버츠 동아시아...\\nENFJ 연결의 힘 사람들 간의 관계를 강조한 책. 데비 글로스터 민음사...\\nESTP 즉흥의 미학 즉흥적으로 즐기는 삶의 아름다움. 스타일리스트 샤론 현대 출판사...\\n"));
        messages.put(new JSONObject()
                .put("role", "user")
                .put("content", userPrompt1));

        messages.put(new JSONObject()
                .put("role","system")
                .put("content",systemPrompt1));
        messages.put(new JSONObject()
                .put("role","system")
                .put("content",systemPrompt2));
        if(userInput.length() > 255){

            userInput = userInput.substring(0, 255);
        }
        messages.put(new JSONObject()
                .put("role","user")
                .put("content",userInput));


        json.put("messages", messages);

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBearerAuth(apiKey); // API 키를 Authorization 헤더가 아닌  X-API-KEY에 추가
//        headers.add("X-API-KEY",apiKey);
        headers.setBearerAuth(apiKey);
        // HTTP 요청 생성
        HttpEntity<String> request = new HttpEntity<>(json.toString(), headers);


        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

        // JSON 응답에서 content 필드만 파싱하여 반환
        JSONObject jsonResponse = new JSONObject(response.getBody());
        JSONArray choices = jsonResponse.getJSONArray("choices");
        // 첫 번째 선택지의 메시지에서 content 가져오기
        String content = choices.getJSONObject(0).getJSONObject("message").getString("content");
        System.out.println("gpt 응답"+content);
        if (content.length() > 255) { // 칼럼의 최대 길이에 맞춰 설정
            content = content.substring(0, 255); // 255자로 자르기
        }

        return content; // content 반환
    }
}
*/
