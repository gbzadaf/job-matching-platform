package com.gabrielf.job_matching_platform.dto.response;

public record LoginResponse(
        String token,
        String tokenType

) {
    public static LoginResponse of(String token) {
        return new LoginResponse(token, "Bearer");

    }
}
