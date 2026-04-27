package com.pogbe.bankingsystem.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequest {
    @Schema(description = "Username", example = "emeka")
    private String username;
    @Schema(description = "Password", example = "<PASSWORD>")
    private String password;
}
