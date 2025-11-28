package com.basic.api.user.domain.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus {

    ACTIVE("활동"),
    DELETED("탈퇴"),
    ;

    private final String text;
}
