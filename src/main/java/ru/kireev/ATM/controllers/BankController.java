package ru.kireev.ATM.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import ru.kireev.ATM.entities.Card;
import ru.kireev.ATM.entities.Client;
import ru.kireev.ATM.entities.Operation;
import ru.kireev.ATM.entities.OperationType;
import ru.kireev.ATM.services.BankService;
import ru.kireev.ATM.services.CardService;
import ru.kireev.ATM.services.ClientService;
import ru.kireev.ATM.services.OperationService;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Controller
@RequestMapping()
@RequiredArgsConstructor
@SessionAttributes(value = {"client", "card"})
public class BankController {

    private final BankService bankService;
    private final CardService cardService;
    private final ClientService clientService;
    private final OperationService operationService;

    @GetMapping("/client")
    public String clientInformation(Principal principal, Model model) {

        Card card = cardService.findByCardNumber(Integer.parseInt(principal.getName()));
        System.out.println("Карта авторизации " + card);

        Client client = card.getClient();
        System.out.println("Клиент " + client);

        model.addAttribute("client", client).addAttribute("helloMessage", helloMessage(client));
        return "clientMainPage";

    }

    @GetMapping("/client/card/{cardLastNumbers}")
    public String cardInformation(@ModelAttribute("client") Client client, @PathVariable("cardLastNumbers") String cardLastNumbers, Model model) {

        Card card = client.getCards()
                .stream()
                .filter(c -> c.getLastNumbers().equals(cardLastNumbers))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Такой карты не существует!"));

        model.addAttribute("card", card);

        System.out.println("Меню карты " + card.getCardNumber());
        return "cardPage";

    }

    @DeleteMapping("/client/card/{cardLastNumbers}")
    public String blockCard(@ModelAttribute("card") Card card, @PathVariable("cardLastNumbers") String cardLastNumbers) {

        cardService.blockCard(card);

        System.out.println("КАРТА " + card.getCardNumber() + " ЗАБЛОКИРОВАНА");
        return "redirect:/client/card/" + cardLastNumbers + "/block";

    }

    @GetMapping("/client/card/{cardLastNumbers}/block")
    public String blockedCardPage(@PathVariable("cardLastNumbers") String cardLastNumbers) {

        return "blockedCardPage";

    }

    @GetMapping("/client/card/{cardLastNumbers}/withdraw")
    public String withdraw(@PathVariable("cardLastNumbers") String cardLastNumbers, Model model) {

        model.addAttribute("operation", new Operation());
        return "withdrawPage";

    }

    @PutMapping("/client/card/{cardLastNumbers}/withdraw")
    public String withdrawMoney(@ModelAttribute("card") Card card,
                                @ModelAttribute("operation") Operation operation,
                                @PathVariable("cardLastNumbers") String cardLastNumbers,
                                SessionStatus sessionStatus) {

        cardService.withdrawMoney(card, new BigDecimal(operation.getAmountOfMoney()));
        operationService.saveOperation(operation.setOperationType(OperationType.WITHDRAWAL).setFromCard(card.getCardNumber()).setDateAndTime(LocalDateTime.now()));
        sessionStatus.setComplete();

        System.out.println("С карты " + card.getCardNumber() + " снято " + operation.getAmountOfMoney());
        return "redirect:/client/";

    }

    @GetMapping("/client/card/{cardLastNumbers}/deposit")
    public String deposit(@PathVariable("cardLastNumbers") String cardLastNumbers, Model model) {

        model.addAttribute("operation", new Operation());
        return "depositPage";

    }

    @PutMapping("/client/card/{cardLastNumbers}/deposit")
    public String addMoney(@ModelAttribute("card") Card card,
                           @ModelAttribute("operation") Operation operation,
                           @PathVariable("cardLastNumbers") String cardLastNumbers,
                           SessionStatus sessionStatus) {

        cardService.putMoneyIntoAccount(card, new BigDecimal(operation.getAmountOfMoney()));
        operationService.saveOperation(operation.setOperationType(OperationType.DEPOSIT).setToCard(card.getCardNumber()).setDateAndTime(LocalDateTime.now()));
        sessionStatus.setComplete();

        System.out.println("На карту " + card.getCardNumber() + " положено " + operation.getAmountOfMoney());
        return "redirect:/client/";

    }

    @GetMapping("/client/card/{cardLastNumbers}/transfer")
    public String transfer(@PathVariable("cardLastNumbers") String cardLastNumbers, Model model) {

        model.addAttribute("operation", new Operation());
        return "transferPage";

    }

    @PutMapping("/client/card/{cardLastNumbers}/transfer")
    public String transferMoney(@ModelAttribute("card") Card cardFrom,
                                @ModelAttribute("operation") Operation operation,
                                @PathVariable("cardLastNumbers") String cardLastNumbers,
                                SessionStatus sessionStatus) {

        Card cardTo = cardService.findByCardNumber(operation.getToCard());
        cardService.transferMoney(cardFrom, cardTo, new BigDecimal(operation.getAmountOfMoney()));
        operationService.saveOperation(operation
                .setOperationType(OperationType.TRANSFER)
                .setToCard(cardTo.getCardNumber())
                .setFromCard(cardFrom.getCardNumber())
                .setDateAndTime(LocalDateTime.now()));
        sessionStatus.setComplete();

        System.out.println("ПЕРЕВОД СРЕДСТВ - " + operation.getAmountOfMoney() + " С КАРТЫ: " + cardTo.getCardNumber() + " НА КАРТУ " + cardFrom.getCardNumber());
        return "redirect:/client/";

    }

    @GetMapping("/client/card/{cardLastNumbers}/pin")
    public String pin(@PathVariable("cardLastNumbers") String cardLastNumbers) {

        return "changePinPage";

    }

    @PutMapping("/client/card/{cardLastNumbers}/pin")
    public String changePin(@ModelAttribute("card") Card cardWithNewPin,
                            @PathVariable("cardLastNumbers") String cardLastNumbers,
                            SessionStatus sessionStatus) {

        cardService.updateCard(cardWithNewPin.setPin(new BCryptPasswordEncoder(12).encode(cardWithNewPin.getPin())));
        sessionStatus.setComplete();

        System.out.println("Пин-код карты " + cardWithNewPin.getCardNumber() + " изменен на " + cardWithNewPin.getPin());
        return "redirect:/client/";

    }

    @GetMapping("/client/card/{cardLastNumbers}/history")
    public String operationHistory() {

        return "historyPage";

    }

    private String helloMessage(Client client) {

        int currentHour = LocalTime.now().getHour();
        String partOfDay;

        if (currentHour > 5 && currentHour <= 11) partOfDay = "Доброе утро";
        else if (currentHour > 11 && currentHour <= 17) partOfDay = "Добрый день";
        else if (currentHour > 17 && currentHour <= 23) partOfDay = "Добрый вечер";
        else partOfDay = "Доброй ночи";

        return partOfDay + ", " + client.getName() + " " + client.getSurname() + "!";

    }

}



