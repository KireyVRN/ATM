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
@SessionAttributes(value = "client")
public class ClientController {

    private final CardService cardService;

    @GetMapping("/client")
    public String clientMenu(Principal principal, Model model) {

        Card card = cardService.getCardByNumber(principal.getName());
        Client client = card.getClient();
        model.addAttribute("client", client).addAttribute("partOfDay", getPartOfDayProperty());
        return "clientPage";

    }

    @GetMapping("/totalBalance")
    public String totalBalance(@ModelAttribute("client") Client client, Model model) {

        BigDecimal totalBalance = client
                .getCards().stream()
                .map(card -> card.getBalance())
                .reduce((balance1, balance2) -> balance1.add(balance2))
                .get();

        model.addAttribute("totalBalance", totalBalance);
        return "totalBalancePage";

    }

    private String getPartOfDayProperty() {

        int currentHour = LocalTime.now().getHour();
        String partOfDay;

        if (currentHour > 5 && currentHour <= 11) partOfDay = "morning";
        else if (currentHour > 11 && currentHour <= 17) partOfDay = "day";
        else if (currentHour > 17 && currentHour <= 23) partOfDay = "evening";
        else partOfDay = "night";

        return String.format("message.greeting.%s", partOfDay);

    }

}