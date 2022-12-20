package com.example.oauthjwt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {

    @GetMapping("/oauth/login")
    public String OAuthLogin(Model model){
        return "/oauth/login";
    }
}
