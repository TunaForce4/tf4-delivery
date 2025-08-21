package com.tf4.delivery.repository.feign.auth.response;

import lombok.Getter;


@Getter
public class AuthFindInfoResponseDto {
    private String role;
    private String slackId;
}
