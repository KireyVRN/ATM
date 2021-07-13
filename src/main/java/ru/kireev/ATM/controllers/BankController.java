package ru.kireev.ATM.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
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
@SessionAttributes(value = {"client", "card"})
public class BankController {

    private final BankService bankService;


    @GetMapping("/client/{clientId}")
    public String clientInformation(@PathVariable("clientId") long id, Model model) {

        Client client = bankService.getClientById(id);
        model.addAttribute("client", client);

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
    public String blockCard(@ModelAttribute("card") Card card,
                            @PathVariable("clientId") long clientId,
                            @PathVariable("cardId") long cardId,
                            Model model) {

        //bankService.blockCard(cardId);
        bankService.blockCard(card);

        System.out.println("КАРТА " + card + " ЗАБЛОКИРОВАНА");

        return "redirect:/client/" + clientId + "/card" + cardId + "/block";

    }

    @GetMapping("/client/{clientId}/card{cardId}/block")
    public String blockedCardPage(@PathVariable("clientId") long clientId,
                                  @PathVariable("cardId") long cardId) {

        return "blockedCardPage";

    }

    //ВЫВОД ДЕНЕГ
    @GetMapping("/client/{clientId}/card{cardId}/withdraw")
    public String withdraw(
            @PathVariable("clientId") long clientId,
            @PathVariable("cardId") long cardId,
            Model model) {

        model.addAttribute("operation", new Operation());

        return "withdrawPage";

    }

    @PutMapping("/client/{clientId}/card{cardId}/withdraw")
    public String withdrawMoney(@ModelAttribute("card") Card card,
                                @ModelAttribute("operation") Operation operation,
                                @PathVariable("clientId") long clientId,
                                @PathVariable("cardId") long cardId,
                                Model model,
                                SessionStatus sessionStatus) {

        bankService.withdrawMoney(card, new BigDecimal(operation.getAmountOfMoney()));
        bankService.saveOperation(operation.setOperationType(OperationType.WITHDRAWAL).setFromCard(card.getCardNumber()).setDateAndTime(LocalDateTime.now()));
        sessionStatus.setComplete();

        System.out.println("КАРТА " + card + ", ВЫВЕДЕНО СО СЧЕТА - " + operation.getAmountOfMoney());

        return "redirect:/client/" + clientId;

    }

    //ПОЛОЖИТЬ ДЕНЬГИ
    @GetMapping("/client/{clientId}/card{cardId}/deposit")
    public String deposit(@PathVariable("clientId") long clientId,
                          @PathVariable("cardId") long cardId,
                          Model model) {

        model.addAttribute("operation", new Operation());

        return "depositPage";

    }

    @PutMapping("/client/{clientId}/card{cardId}/deposit")
    public String addMoney(@ModelAttribute("card") Card card,
                           @ModelAttribute("operation") Operation operation,
                           @PathVariable("clientId") long clientId,
                           @PathVariable("cardId") long cardId,
                           SessionStatus sessionStatus) {

        bankService.putMoneyIntoAccount(card, new BigDecimal(operation.getAmountOfMoney()));
        bankService.saveOperation(operation.setOperationType(OperationType.DEPOSIT).setToCard(card.getCardNumber()).setDateAndTime(LocalDateTime.now()));
        sessionStatus.setComplete();

        System.out.println("КАРТА " + card + ", ПОПОЛНЕН СЧЕТ - " + operation.getAmountOfMoney());

        return "redirect:/client/" + clientId;

    }

    //ПЕРЕВЕСТИ ДЕНЬГИ
    @GetMapping("/client/{clientId}/card{cardId}/transfer")
    public String transfer(@PathVariable("clientId") long clientId,
                           @PathVariable("cardId") long cardId,
                           Model model) {

        model.addAttribute("operation", new Operation());

        return "transferPage";

    }

    @PutMapping("/client/{clientId}/card{cardId}/transfer")
    public String transferMoney(@ModelAttribute("card") Card cardFrom,
                                @ModelAttribute("operation") Operation operation,
                                @PathVariable("clientId") long clientId,
                                @PathVariable("cardId") long cardId,
                                SessionStatus sessionStatus) {

        Card cardTo = bankService.getCardByNumber(operation.getToCard());
        bankService.transferMoney(cardFrom, cardTo, new BigDecimal(operation.getAmountOfMoney()));
        bankService.saveOperation(operation
                .setOperationType(OperationType.TRANSFER)
                .setToCard(cardTo.getCardNumber())
                .setFromCard(cardFrom.getCardNumber())
                .setDateAndTime(LocalDateTime.now()));
        sessionStatus.setComplete();

        System.out.println("ПЕРЕВОД СРЕДСТВ - " + operation.getAmountOfMoney() + " С КАРТЫ: " + cardTo.getCardNumber() + " НА КАРТУ " + cardFrom.getCardNumber());

        return "redirect:/client/" + clientId;

    }

    //ИЗМЕНИТЬ ПИН КОД
    @GetMapping("/client/{clientId}/card{cardId}/pin")
    public String pin(@PathVariable("clientId") long clientId,
                      @PathVariable("cardId") long cardId,
                      Model model) {

        return "changePinPage";

    }

    @PutMapping("/client/{clientId}/card{cardId}/pin")
    public String changePin(@ModelAttribute("card") Card cardWithNewPin,
                            @PathVariable("clientId") long clientId,
                            @PathVariable("cardId") long cardId,
                            SessionStatus sessionStatus) {

        bankService.updateCard(cardWithNewPin);
        sessionStatus.setComplete();

        System.out.println("КАРТА " + cardWithNewPin.getCardNumber() + " ИЗМЕНЕН ПИН КОД - " + cardWithNewPin.getPin());

        return "redirect:/client/" + clientId;

    }

}



