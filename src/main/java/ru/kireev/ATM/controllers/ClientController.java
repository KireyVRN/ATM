package ru.kireev.ATM.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import ru.kireev.ATM.entities.Card;
import ru.kireev.ATM.entities.Client;
import ru.kireev.ATM.services.CardService;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalTime;


@Controller
@RequiredArgsConstructor
@SessionAttributes(value = {"client", "card"})
public class ClientController {

    private final CardService cardService;

    @GetMapping("/client")
    public String clientMenu(Principal principal, Model model) {

        Card card = cardService.findByCardNumber(principal.getName());
        System.out.println("Карта авторизации " + card);

        Client client = card.getClient();
        System.out.println("Клиент " + client);

        model.addAttribute("client", client).addAttribute("helloMessage", helloMessage(client));
        return "clientPage";

    }

    @GetMapping("/totalBalance")
    public String totalBalance(@ModelAttribute("client") Client client, Model model) {

        BigDecimal totalBalance = client.getCards().stream().map(card -> card.getBalance()).reduce((balance1, balance2) -> balance1.add(balance2)).get();

        model.addAttribute("totalBalance", totalBalance);
        return "totalBalancePage";

    }

    private String helloMessage(Client client) {

        int currentHour = LocalTime.now().getHour();
        String partOfDay;

        if (currentHour > 5 && currentHour <= 11) partOfDay = "Доброе утро";
        else if (currentHour > 11 && currentHour <= 17) partOfDay = "Добрый день";
        else if (currentHour > 17 && currentHour <= 23) partOfDay = "Добрый вечер";
        else partOfDay = "Доброй ночи";

        return String.format("%s, %s %s!", partOfDay, client.getName(), client.getSurname());

    }

}