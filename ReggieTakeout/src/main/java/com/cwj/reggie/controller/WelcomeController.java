package com.cwj.reggie.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;

@Controller
public class WelcomeController {

    @GetMapping("/")
    public void index(HttpServletResponse response) throws Exception{
        response.sendRedirect("/backend/page/login/login.html");
    }
}
