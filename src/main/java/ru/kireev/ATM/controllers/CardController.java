package ru.kireev.ATM.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/client/card")
@SessionAttributes(value = {"client", "card"})
public class CardController {

    private final BankService bankService;
    private final CardService cardService;
    private final ClientService clientService;
    private final OperationService operationService;

    @GetMapping("/{cardLastNumbers}")
    public String cardMenu(@ModelAttribute("client") Client client, @PathVariable("cardLastNumbers") String cardLastNumbers, Model model) {

        Card card = client.getCards()
                .stream()
                .filter(c -> c.getLastNumbers().equals(cardLastNumbers))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Такой карты не существует!"));

        model.addAttribute("card", card);

        System.out.println("Меню карты " + card.getCardNumber());
        return "cardPage";

    }

    @Transactional
    @DeleteMapping("/{cardLastNumbers}")
    public String blockCard(@ModelAttribute("card") Card card, @PathVariable("cardLastNumbers") String cardLastNumbers) {

        cardService.blockCard(card);

        System.out.println("КАРТА " + card.getCardNumber() + " ЗАБЛОКИРОВАНА");
        return "redirect:/client/card/" + cardLastNumbers + "/block";

    }

    @GetMapping("/{cardLastNumbers}/block")
    public String blockedCardPage(@ModelAttribute(name = "card") Card card, @PathVariable("cardLastNumbers") String cardLastNumbers, Model model, Principal principal) {

        boolean currentCard = String.valueOf(card.getCardNumber()).equals(principal.getName());

        model.addAttribute("message", "Карта заблокирована").addAttribute("currentCard", currentCard);

        System.out.println(principal.getName());

        return "successfulActionPage";

        //return "redirect:/successfulAction";

    }


    @GetMapping("/{cardLastNumbers}/withdraw")
    public String withdraw(@PathVariable("cardLastNumbers") String cardLastNumbers, Model model) {

        model.addAttribute("operation", new Operation());
        return "withdrawPage";

    }

    @Transactional
    @PutMapping("/{cardLastNumbers}/withdraw")
    public String withdrawMoney(@ModelAttribute("card") Card card,
                                @ModelAttribute("operation") Operation operation,
                                @PathVariable("cardLastNumbers") String cardLastNumbers,
                                Model model,
                                SessionStatus sessionStatus) {

        cardService.withdrawMoney(card, new BigDecimal(operation.getAmountOfMoney()));
        operationService.saveOperation(operation.setOperationType(OperationType.WITHDRAWAL)
                .setFromCard(card.getCardNumber())
                .setDateAndTime(LocalDateTime.now())
                .setCard(card));

        sessionStatus.setComplete();

        model.addAttribute("message", "Заберите деньги");

        System.out.println("С карты " + card.getCardNumber() + " снято " + operation.getAmountOfMoney());

        return "successfulActionPage";

    }

    @GetMapping("/{cardLastNumbers}/deposit")
    public String deposit(@PathVariable("cardLastNumbers") String cardLastNumbers, Model model) {

        model.addAttribute("operation", new Operation());
        return "depositPage";

    }

    @Transactional
    @PutMapping("/{cardLastNumbers}/deposit")
    public String addMoney(@ModelAttribute("card") Card card,
                           @ModelAttribute("operation") Operation operation,
                           @PathVariable("cardLastNumbers") String cardLastNumbers,
                           SessionStatus sessionStatus,
                           Model model) {

        cardService.putMoneyIntoAccount(card, new BigDecimal(operation.getAmountOfMoney()));
        operationService.saveOperation(operation.setOperationType(OperationType.DEPOSIT)
                .setToCard(card.getCardNumber())
                .setDateAndTime(LocalDateTime.now())
                .setCard(card));

        sessionStatus.setComplete();

        model.addAttribute("message", "Баланс карты пополнен");

        System.out.println("На карту " + card.getCardNumber() + " положено " + operation.getAmountOfMoney());

        return "successfulActionPage";

    }

    @GetMapping("/{cardLastNumbers}/transfer")
    public String transfer(@PathVariable("cardLastNumbers") String cardLastNumbers, Model model) {

        model.addAttribute("operation", new Operation());
        return "transferPage";

    }

    @Transactional
    @PutMapping("/{cardLastNumbers}/transfer")
    public String transferMoney(@ModelAttribute("card") Card cardFrom,
                                @ModelAttribute("operation") Operation operation,
                                @PathVariable("cardLastNumbers") String cardLastNumbers,
                                SessionStatus sessionStatus,
                                Model model) {

        Card cardTo = cardService.findByCardNumber(operation.getToCard());
        cardService.transferMoney(cardFrom, cardTo, new BigDecimal(operation.getAmountOfMoney()));

        operationService.saveOperation(operation
                .setOperationType(OperationType.TRANSFER)
                .setFromCard(cardFrom.getCardNumber())
                .setToCard(cardTo.getCardNumber())
                .setDateAndTime(LocalDateTime.now())
                .setCard(cardFrom));

        operationService.saveOperation(new Operation()
                .setOperationType(OperationType.TRANSFER)
                .setFromCard(cardFrom.getCardNumber())
                .setToCard(cardTo.getCardNumber())
                .setAmountOfMoney(operation.getAmountOfMoney())
                .setDateAndTime(LocalDateTime.now())
                .setCard(cardTo));

        sessionStatus.setComplete();

        model.addAttribute("message", "Перевод выполнен");

        System.out.println("ПЕРЕВОД СРЕДСТВ - " + operation.getAmountOfMoney() + " С КАРТЫ: " + cardFrom.getCardNumber() + " НА КАРТУ " + cardTo.getCardNumber());

        return "successfulActionPage";

    }

    @GetMapping("/{cardLastNumbers}/pin")
    public String pin(@PathVariable("cardLastNumbers") String cardLastNumbers) {

        return "changePinPage";

    }

    @Transactional
    @PutMapping("/{cardLastNumbers}/pin")
    public String changePin(@ModelAttribute("card") Card cardWithNewPin,
                            @PathVariable("cardLastNumbers") String cardLastNumbers,
                            SessionStatus sessionStatus,
                            Model model) {

        cardService.updateCard(cardWithNewPin.setPin(new BCryptPasswordEncoder(12).encode(cardWithNewPin.getPin())));
        sessionStatus.setComplete();

        operationService.saveOperation(new Operation().setOperationType(OperationType.PIN_CHANGE)
                .setDateAndTime(LocalDateTime.now())
                .setCard(cardWithNewPin));

        model.addAttribute("message", "Пин-код изменен");

        System.out.println("Пин-код карты " + cardWithNewPin.getCardNumber() + " изменен на " + cardWithNewPin.getPin());

        return "successfulActionPage";

    }

    @GetMapping("/{cardLastNumbers}/history")
    public String operationHistory(@ModelAttribute(name = "card") Card card, Model model) {

        System.out.println(card.getOperations());

        return "historyPage";

    }
}



