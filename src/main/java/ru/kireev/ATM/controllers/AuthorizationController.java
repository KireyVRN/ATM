package ru.kireev.ATM.controllers;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Locale;


@Controller
public class AuthorizationController {

    @GetMapping("/")
    public String helloPage(Model model) {

        model.addAttribute("locale", LocaleContextHolder.getLocale().toString());
        return "helloPage";

    }

    @PostMapping("/")
    public String helloPageWithParams(@RequestParam("lang") String lang, Model model) {

        LocaleContextHolder.setLocale(new Locale(lang));
        model.addAttribute("locale", LocaleContextHolder.getLocale().toString());
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

    @GetMapping("/loginFailed")
    public String loginFailed() {

        return "loginFailedPage";

    }

}