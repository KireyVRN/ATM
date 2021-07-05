package ru.kireev.ATM.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kireev.ATM.entities.Card;
import ru.kireev.ATM.entities.Client;
import ru.kireev.ATM.entities.Operation;
import ru.kireev.ATM.entities.OperationType;
import ru.kireev.ATM.services.BankService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping()
@RequiredArgsConstructor
@SessionAttributes("client")
//@SessionAttributes(value = {"client","cards"})
public class BankController {

    private final BankService bankService;


    @GetMapping("/client/{clientId}")
    public String clientInformation(@PathVariable("clientId") long id, Model model) {

        Client client = bankService.getClientById(id);
        Set<Card> cards = client.getCards();

        model.addAttribute("client", client);
        //model.addAttribute("cards", cards);

        System.out.println("ГЛАВНАЯ " + client);
        return "clientMainPage";

    }

    @GetMapping("/client/{clientId}/card{cardId}")
    public String cardInformation(@ModelAttribute("client") Client client,
                                  @PathVariable("clientId") long clientId,
                                  @PathVariable("cardId") long cardId,
                                  Model model) {

        Card card = client.getCards()
                .stream()
                .filter(c -> c.getId() == cardId)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Такой карты не существует!"));

        model.addAttribute("card", card);

        System.out.println("КАРТA - " + card);
        return "cardPage";

    }

    //БЛОКИРОВКА КАРТ
    @DeleteMapping("/client/{clientId}/card{cardId}")
    public String blockCard(@PathVariable("clientId") long clientId,
                            @PathVariable("cardId") long cardId,
                            Model model) {

        bankService.blockCard(cardId);

        return "redirect:/client/" + clientId + "/card" + cardId + "/block";

    }

    @GetMapping("/client/{clientId}/card{cardId}/block")
    public String blockedCardPage(@PathVariable("clientId") long clientId,
                                  @PathVariable("cardId") long cardId,
                                  Model model) {

        return "blockedCardPage";

    }

    //ВЫВОД ДЕНЕГ
    @GetMapping("/client/{clientId}/card{cardId}/withdraw")
    public String withdraw(@PathVariable("clientId") long clientId,
                           @PathVariable("cardId") long cardId,
                           Model model) {

        Card card = bankService.getCardById(cardId);

        model.addAttribute("card", card);

        //Operation operation = new Operation().setAmountOfMoney("0");
        model.addAttribute("operation", new Operation());

        return "withdrawPage";

    }

    @PutMapping("/client/{clientId}/card{cardId}/withdraw")
    public String withdrawMoney(@ModelAttribute("operation") Operation operation,
                                @PathVariable("clientId") long clientId,
                                @PathVariable("cardId") long cardId,
                                Model model) {

        System.out.println("MONEY TO WITHDRAW - " + operation.getAmountOfMoney());

        Card card = bankService.getCardById(cardId);
        bankService.withdrawMoney(card, new BigDecimal(operation.getAmountOfMoney()));

        operation.setOperationType(OperationType.WITHDRAWAL).setFromCard(card.getCardNumber()).setDateAndTime(LocalDateTime.now());
        bankService.saveOperation(operation);

        return "redirect:/client/" + clientId;

    }

    //ПОЛОЖИТЬ ДЕНЬГИ
    @GetMapping("/client/{clientId}/card{cardId}/deposit")
    public String deposit(@PathVariable("clientId") long clientId,
                          @PathVariable("cardId") long cardId,
                          Model model) {

        Card card = bankService.getCardById(cardId);
        model.addAttribute("card", card);
        model.addAttribute("operation", new Operation());

        return "depositPage";

    }

    @PutMapping("/client/{clientId}/card{cardId}/deposit")
    public String addMoney(@ModelAttribute("operation") Operation operation,
                           @PathVariable("clientId") long clientId,
                           @PathVariable("cardId") long cardId) {

        System.out.println("MONEY TO ADD - " + operation.getAmountOfMoney());


        Card card = bankService.getCardById(cardId);
        bankService.putMoneyIntoAccount(card, new BigDecimal(operation.getAmountOfMoney()));

        operation.setOperationType(OperationType.DEPOSIT).setToCard(card.getCardNumber()).setDateAndTime(LocalDateTime.now());
        bankService.saveOperation(operation);

        return "redirect:/client/" + clientId;

    }

    @GetMapping("/client/{clientId}/card{cardId}/transfer")
    public String transfer(@PathVariable("clientId") long clientId,
                           @PathVariable("cardId") long cardId,
                           Model model) {

        Card card = bankService.getCardById(cardId);
        model.addAttribute("card", card);
        model.addAttribute("operation", new Operation());

        return "transferPage";

    }

    @PutMapping("/client/{clientId}/card{cardId}/transfer")
    public String transferMoney(@ModelAttribute("operation") Operation operation,
                                @PathVariable("clientId") long clientId,
                                @PathVariable("cardId") long cardId) {

        Card cardFrom = bankService.getCardById(cardId);
        Card cardTo = bankService.getCardByNumber(operation.getToCard());

        System.out.println("MONEY TO TRANSFER - " + operation.getAmountOfMoney() + " TO CLIENT: " + cardTo.getCardNumber());

        bankService.transferMoney(cardFrom, cardTo, new BigDecimal(operation.getAmountOfMoney()));

        operation.setOperationType(OperationType.TRANSFER).setToCard(cardTo.getCardNumber()).setFromCard(cardFrom.getCardNumber()).setDateAndTime(LocalDateTime.now());
        bankService.saveOperation(operation);

        return "redirect:/client/" + clientId;

    }

    @GetMapping("/client/{clientId}/card{cardId}/pin")
    public String pin(@PathVariable("clientId") long clientId,
                      @PathVariable("cardId") long cardId,
                      Model model) {

        Card card = bankService.getCardById(cardId);
        model.addAttribute("card", card);
        //model.addAttribute("operation", new Operation());

        return "changePinPage";

    }

    @PutMapping("/client/{clientId}/card{cardId}/pin")
    public String changePin(@ModelAttribute("card") Card newPin,
                            @PathVariable("clientId") long clientId,
                            @PathVariable("cardId") long cardId) {


        Card card = bankService.getCardById(cardId);
        bankService.changePin(card, newPin.getPin());

        System.out.println("КАРТА" + card.getCardNumber() + "PIN CHANGED - " + card.getPin());

        return "redirect:/client/" + clientId;

    }

}



