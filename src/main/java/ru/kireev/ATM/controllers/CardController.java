package ru.kireev.ATM.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import ru.kireev.ATM.services.BankService;
import ru.kireev.ATM.services.CardService;
import ru.kireev.ATM.services.ClientService;
import ru.kireev.ATM.services.OperationService;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


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

    @DeleteMapping("/{cardLastNumbers}")
    public String blockCard(@ModelAttribute("card") Card card, @PathVariable("cardLastNumbers") String cardLastNumbers, Model model, Principal principal) {

        cardService.blockCard(card);

        boolean currentCard = String.valueOf(card.getCardNumber()).equals(principal.getName());
        model.addAttribute("message", "Карта заблокирована").addAttribute("currentCard", currentCard);

        System.out.println("КАРТА " + card.getCardNumber() + " ЗАБЛОКИРОВАНА");
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

        if (!card.hasEnoughMoney(operation.getAmountOfMoney())) {

            bindingResult.addError(new ObjectError("card", "На карте недостаточно средств"));

        }


        if (bindingResult.hasErrors()) {

            return "withdrawPage";

        }

        cardService.withdrawMoney(card, operation.getAmountOfMoney());
        operationService.saveOperation(operation.setOperationType(OperationType.WITHDRAWAL)
                .setFromCard(card.getCardNumber())
                .setDateAndTime(LocalDateTime.now())
                .setCard(card));

        sessionStatus.setComplete();

        model.addAttribute("message", "Заберите наличные");

        System.out.println("С карты " + card.getCardNumber() + " снято " + operation.getAmountOfMoney());
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

    @PutMapping("/{cardLastNumbers}/transfer")
    public String transferMoney(@ModelAttribute("card") Card cardFrom,
                                @ModelAttribute("operation") @Valid Operation operation,
                                BindingResult bindingResult,
                                @PathVariable("cardLastNumbers") String cardLastNumbers,
                                SessionStatus sessionStatus,
                                Model model) {

        if (!cardService.cardExists(operation.getToCard())) {

            bindingResult.addError(new ObjectError("operation", "Такой карты не существует"));

        }

        if (cardFrom.getCardNumber().equals(operation.getToCard())) {

            bindingResult.addError(new ObjectError("operation", "С этой карты производится операция, введите пожалуйста номер другой карты"));

        }

        if (bindingResult.hasErrors()) {

            return "transferPage";


        }


        Card cardTo = cardService.findByCardNumber(operation.getToCard());
        cardService.transferMoney(cardFrom, cardTo, operation.getAmountOfMoney());

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
        model.addAttribute("message", "Перевод успешно произведен");
        System.out.println("ПЕРЕВОД СРЕДСТВ - " + operation.getAmountOfMoney() + " С КАРТЫ: " + cardFrom.getCardNumber() + " НА КАРТУ " + cardTo.getCardNumber());
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

        if (!cardWithNewPin.getPin().matches("\\d{4}")) {

            bindingResult.addError(new ObjectError("card", "Пин-код должен состоять из 4 цифр"));

        }

        if (bindingResult.hasErrors()) {

            bindingResult.getAllErrors().stream().forEach(x -> System.out.println("ERROR - " + x.getObjectName() + " " + x.getDefaultMessage()));
            return "changePinPage";

        }

        cardService.updateOrSaveCard(cardWithNewPin.setPin(new BCryptPasswordEncoder(12).encode(cardWithNewPin.getPin())));
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

        List<Operation> history = card.getOperations().stream().sorted((o1, o2) -> {
            if (o1.getDateAndTime().isBefore(o2.getDateAndTime())) return 1;
            else if (o1.getDateAndTime().isAfter(o2.getDateAndTime())) return -1;
            else return 0;
        }).limit(10).collect(Collectors.toList());

        model.addAttribute("history", history);

        return "historyPage";

    }
}



