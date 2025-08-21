package com.tf4.delivery.repository.feign.auth.response;

import lombok.Getter;

import java.math.BigInteger;
import java.util.UUID;


@Getter
public class AuthFindInfoResponseDto {
    private String role;
    private String slackId;
}
