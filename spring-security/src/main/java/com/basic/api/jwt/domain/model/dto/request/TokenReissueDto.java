package com.basic.api.jwt.domain.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenReissueDto {
    private String refreshToken;
}
