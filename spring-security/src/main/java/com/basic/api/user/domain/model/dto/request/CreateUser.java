package com.basic.api.user.domain.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateUser {

    private String email;
    private String password;
}
