package ru.kireev.ATM.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class AuthorizationController {

    @GetMapping("/")
    public String helloPage() {

        return "helloPage";

    }

    @GetMapping("/login")
    public String login() {

        return "loginPage";

    }

    @GetMapping("/logoutSuccess")
    public String logout() {

        return "logoutPage";

    }

}