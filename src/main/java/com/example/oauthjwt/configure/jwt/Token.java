package com.example.oauthjwt.configure.jwt;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class Token {
    private String token;
    private String refreshToken;

    public Token(String token, String refreshToken){
        this.token = token;
        this.refreshToken = refreshToken;
    }
}
