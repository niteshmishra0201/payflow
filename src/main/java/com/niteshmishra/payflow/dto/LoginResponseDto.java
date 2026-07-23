package com.niteshmishra.payflow.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto {
    private String token;
    private String tokenType = "Bearer";

    public LoginResponseDto(String token) {
        this.token = token;
    }
}