package com.basic.api.user.domain.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Provider {

    KAKAO("Kakao"),
    APPLE("apple"),
    ;

    private final String text;
}
