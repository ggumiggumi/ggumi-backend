package com.uplus.ggumi.domain.child;

public enum Gender {

    MALE("남아"),
    FEMALE("여아")
    ;

    private String description;

    Gender(String description) {
        this.description = description;
    }
}

