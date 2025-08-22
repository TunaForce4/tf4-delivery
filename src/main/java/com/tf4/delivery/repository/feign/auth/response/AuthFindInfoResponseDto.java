package com.tf4.delivery.repository.feign.auth.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class AuthFindInfoResponseDto {
    private UUID userId;
    private String userLoginId;
    private String username;
    private String password;
    private UserRole role;
    private String slackId;
    private String tel;
}
