package com.uplus.ggumi.dto.history;

public enum MBTIType {
    INTJ("전략가", new String[]{"#목표 지향적", "#논리적 사고", "#조용한 관찰자"}),
    INTP("탐구자", new String[]{"#논리적 사고", "#혁신적", "#호기심 많은"}),
    ENTJ("지휘관", new String[]{"#리더십", "#결단력 있는", "#전략적 사고"}),
    ENTP("변론가", new String[]{"#토론 즐김", "#발상가", "#지적 호기심"}),

    INFJ("옹호자", new String[]{"#이타주의자", "#직관적 통찰", "#책임감"}),
    INFP("중재자", new String[]{"#공감능력", "#상상력", "#이상주의"}),
    ENFJ("주도자", new String[]{"#친화력", "#감성적 리더십", "#사교적"}),
    ENFP("활동가", new String[]{"#창의적", "#열정적", "#낙천적"}),

    ISTJ("현실주의자", new String[]{"#성실함", "#책임감", "#신뢰성"}),
    ISFJ("수호자", new String[]{"#헌신적", "#신중함", "#협력적"}),
    ESTJ("경영자", new String[]{"#현실적", "#조직적", "#책임감"}),
    ESFJ("친화자", new String[]{"#사교성", "#관심있음", "#협력적"}),

    ISTP("장인", new String[]{"#실용적", "#논리적", "#위기 관리"}),
    ISFP("모험가", new String[]{"#예술적 감각", "#자유로운 영혼", "#즉흥적"}),
    ESTP("사업가", new String[]{"#모험심", "#실용적", "#결단력"}),
    ESFP("연예인", new String[]{"#사교적", "#유머감각", "#즉흥적"});

    private final String description;
    private final String[] tags;

    MBTIType(String description, String[] tags) {
        this.description = description;
        this.tags = tags;
    }

    public String getDescription() {
        return description;
    }

    public String[] getTags() {
        return tags;
    }
}
