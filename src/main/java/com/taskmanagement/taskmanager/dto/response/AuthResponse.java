package com.taskmanagement.taskmanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
//    private String accessToken;
//    private String refreshToken;
}
