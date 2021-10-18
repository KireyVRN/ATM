package ru.kireev.ATM.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import ru.kireev.ATM.entities.Card;
import ru.kireev.ATM.entities.Client;
import ru.kireev.ATM.entities.Operation;
import ru.kireev.ATM.entities.OperationType;
import ru.kireev.ATM.services.CardService;
import ru.kireev.ATM.services.OperationService;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;


@Controller
@RequiredArgsConstructor
@RequestMapping("/client/card")
@SessionAttributes(value = {"client", "card"})
public class CardController {

    private final CardService cardService;
    private final OperationService operationService;

    @GetMapping("/{cardLastNumbers}")
    public String cardMenu(@ModelAttribute("client") Client client, @PathVariable("cardLastNumbers") String cardLastNumbers, Model model) {

        Card card = client.getCards()
                .stream()
                .filter(c -> c.getLastNumbers().equals(cardLastNumbers))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Card doesn't exist!"));

        model.addAttribute("card", card);
        return "cardPage";

    }

    @GetMapping("/{cardLastNumbers}/blockConfirmation")
    public String blockCardConfirmation(@PathVariable("cardLastNumbers") String cardLastNumbers) {

        return "blockCardConfirmationPage";

    }

    @DeleteMapping("/{cardLastNumbers}")
    public String blockCard(@ModelAttribute("card") Card card,
                            @PathVariable("cardLastNumbers") String cardLastNumbers,
                            Model model, Principal principal,
                            SessionStatus sessionStatus) {

        cardService.blockCard(card);
        boolean currentCard = card.getCardNumber().equals(principal.getName());
        model.addAttribute("currentCard", currentCard).addAttribute("messageProperty", "message.cardBlock");
        sessionStatus.setComplete();
        return "successfulActionPage";

    }

    @GetMapping("/{cardLastNumbers}/withdraw")
    public String withdraw(@PathVariable("cardLastNumbers") String cardLastNumbers, Model model) {

        model.addAttribute("operation", new Operation());
        return "withdrawPage";

    }

    @PutMapping("/{cardLastNumbers}/withdraw")
    public String withdrawMoney(@ModelAttribute("card") Card card,
                                @ModelAttribute("operation") @Valid Operation operation,
                                BindingResult bindingResult,
                                @PathVariable("cardLastNumbers") String cardLastNumbers,
                                Model model,
                                SessionStatus sessionStatus) {

        if (bindingResult.hasErrors()) {
            return "withdrawPage";
        }

        try {
            cardService.withdrawMoney(card, operation.getAmountOfMoney());
        } catch (IllegalArgumentException e) {
            bindingResult.addError(new ObjectError("error", e.getMessage()));
            return "withdrawPage";
        }

        operationService.saveOperation(operation
                .setOperationType(OperationType.WITHDRAW)
                .setFromCardNumber(card.getCardNumber())
                .setDateAndTime(LocalDateTime.now())
                .setCard(card));

        sessionStatus.setComplete();
        model.addAttribute("messageProperty", "message.withdraw.success");
        return "successfulActionPage";

    }

    @GetMapping("/{cardLastNumbers}/deposit")
    public String deposit(@PathVariable("cardLastNumbers") String cardLastNumbers, Model model) {

        model.addAttribute("operation", new Operation());
        return "depositPage";

    }

    @PutMapping("/{cardLastNumbers}/deposit")
    public String addMoney(@ModelAttribute("card") Card card,
                           @ModelAttribute("operation") @Valid Operation operation,
                           BindingResult bindingResult,
                           @PathVariable("cardLastNumbers") String cardLastNumbers,
                           SessionStatus sessionStatus,
                           Model model) {

        if (bindingResult.hasErrors()) {
            return "depositPage";
        }

        cardService.putMoneyIntoAccount(card, operation.getAmountOfMoney());
        operationService.saveOperation(operation
                .setOperationType(OperationType.DEPOSIT)
                .setToCardNumber(card.getCardNumber())
                .setDateAndTime(LocalDateTime.now())
                .setCard(card));

        sessionStatus.setComplete();
        model.addAttribute("messageProperty", "message.deposit.success");
        return "successfulActionPage";

    }

    @GetMapping("/{cardLastNumbers}/transfer")
    public String transfer(@PathVariable("cardLastNumbers") String cardLastNumbers, Model model) {

        model.addAttribute("operation", new Operation());
        return "transferPage";

    }

    @PutMapping("/{cardLastNumbers}/transfer")
    public String transferMoney(@ModelAttribute("card") Card cardFrom,
                                @ModelAttribute("operation") @Valid Operation operation,
                                BindingResult bindingResult,
                                @PathVariable("cardLastNumbers") String cardLastNumbers,
                                SessionStatus sessionStatus,
                                Model model) {

        if (bindingResult.hasErrors()) {
            return "transferPage";
        }

        try {
            cardService.transferMoney(cardFrom, operation.getToCardNumber(), operation.getAmountOfMoney());
        } catch (IllegalArgumentException | NoSuchElementException e) {
            bindingResult.addError(new ObjectError("exception", e.getMessage()));
            return "transferPage";
        }

        Card cardTo = cardService.getCardByNumber(operation.getToCardNumber());

        operationService.saveOperation(operation
                .setOperationType(OperationType.TRANSFER)
                .setFromCardNumber(cardFrom.getCardNumber())
                .setDateAndTime(LocalDateTime.now())
                .setCard(cardFrom));

        operationService.saveOperation(new Operation()
                .setOperationType(OperationType.TRANSFER)
                .setFromCardNumber(cardFrom.getCardNumber())
                .setToCardNumber(cardTo.getCardNumber())
                .setAmountOfMoney(operation.getAmountOfMoney())
                .setDateAndTime(LocalDateTime.now())
                .setCard(cardTo));

        sessionStatus.setComplete();
        model.addAttribute("messageProperty", "message.transfer.success");
        return "successfulActionPage";

    }

    @GetMapping("/{cardLastNumbers}/pin")
    public String pin(@PathVariable("cardLastNumbers") String cardLastNumbers) {

        return "changePinPage";

    }

    @PutMapping("/{cardLastNumbers}/pin")
    public String changePin(@PathVariable("cardLastNumbers") String cardLastNumbers,
                            @ModelAttribute("card") Card cardWithNewPin,
                            BindingResult bindingResult,
                            SessionStatus sessionStatus,
                            Model model) {

        if (bindingResult.hasErrors()) {
            return "changePinPage";
        }

        try {
            cardService.changePin(cardWithNewPin);
        } catch (IllegalArgumentException e) {
            bindingResult.addError(new ObjectError("error", e.getMessage()));
            return "changePinPage";
        }

        model.addAttribute("messageProperty", "message.pinChange.success");
        return "successfulActionPage";

    }

    @GetMapping("/{cardLastNumbers}/history")
    public String operationHistory(@ModelAttribute(name = "card") Card card, Model model) {

        model.addAttribute("history", operationService.getLastOperationsByCard(card));
        return "historyPage";

    }

}