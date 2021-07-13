package ru.kireev.ATM.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kireev.ATM.entities.Card;
import ru.kireev.ATM.entities.Client;
import ru.kireev.ATM.entities.Operation;
import ru.kireev.ATM.repositories.CardRepository;
import ru.kireev.ATM.repositories.ClientRepository;
import ru.kireev.ATM.repositories.OperationRepository;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BankService {

    private final CardRepository cardRepository;
    private final ClientRepository clientRepository;
    private final OperationRepository operationRepository;

//    public List<Client> getAllClients() {
//        return clientRepository.findAll();
//    }
//
//    public List<Card> getAllCards() {
//        return cardRepository.findAll();
//    }
//
//    public List<Operation> getAllOperations() {
//        return operationRepository.findAll();
//    }
//    public Card getCardById(long id) {
//
//        return cardRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Такой карты не существует!"));
//
//    }
//
//    public void blockCard(long id) {
//        cardRepository.deleteById(id);
//    }

    public void updateCard(Card card) {

        cardRepository.saveAndFlush(card);

    }

    public Client getClientById(long id) {

        return clientRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Такого пользователя не существует!"));

    }

    public Card getCardByNumber(int cardNumber) {

        return cardRepository.findByCardNumber(cardNumber).orElseThrow(() -> new NoSuchElementException("Такой карты не существует!"));

    }

    public void blockCard(Card card) {

        cardRepository.deleteById(card.getId());

    }

    public void putMoneyIntoAccount(Card card, BigDecimal amountOfMoney) {

        card.setBalance(card.getBalance().add(amountOfMoney));
        cardRepository.saveAndFlush(card);

    }

    public void withdrawMoney(Card card, BigDecimal amountOfMoney) {

        card.setBalance(card.getBalance().subtract(amountOfMoney));
        cardRepository.saveAndFlush(card);

    }


    public void saveOperation(Operation operation) {

        operationRepository.saveAndFlush(operation);

    }

    public void transferMoney(Card cardFrom, Card cardTo, BigDecimal amountOfMoney) {

        cardFrom.setBalance(cardFrom.getBalance().subtract(amountOfMoney));
        cardTo.setBalance(cardTo.getBalance().add(amountOfMoney));
        cardRepository.saveAndFlush(cardFrom);
        cardRepository.saveAndFlush(cardTo);

    }

    @PostConstruct
    private void init() {

        Card card1 = cardRepository.save(new Card().setCardNumber(11111111).setBalance(BigDecimal.valueOf(300000.34)).setPin(1111));
        Card card2 = cardRepository.save(new Card().setCardNumber(22222222).setBalance(BigDecimal.valueOf(450000.00)).setPin(2222));
        Card card3 = cardRepository.save(new Card().setCardNumber(33333333).setBalance(BigDecimal.valueOf(110000.56)).setPin(3333));
        Card card4 = cardRepository.save(new Card().setCardNumber(44444444).setBalance(BigDecimal.valueOf(666666.32)).setPin(4444));
        Card card5 = cardRepository.save(new Card().setCardNumber(55555555).setBalance(BigDecimal.valueOf(7300000.00)).setPin(5555));
        Card card6 = cardRepository.save(new Card().setCardNumber(66666666).setBalance(BigDecimal.valueOf(777300000.98)).setPin(6666));
        Card card7 = cardRepository.save(new Card().setCardNumber(77777777).setBalance(BigDecimal.valueOf(300000.77)).setPin(7777));
        Card card8 = cardRepository.save(new Card().setCardNumber(88888888).setBalance(BigDecimal.valueOf(7000.54)).setPin(8888));
        Card card9 = cardRepository.save(new Card().setCardNumber(99999999).setBalance(BigDecimal.valueOf(4568.22)).setPin(9999));

        clientRepository.save(new Client().setName("Василий").addCard(card1).addCard(card2));
        clientRepository.save(new Client().setName("Мария").addCard(card3).addCard(card4));
        clientRepository.save(new Client().setName("Jhon").addCard(card5).addCard(card6).addCard(card7).addCard(card8));
        clientRepository.save(new Client().setName("Яша").addCard(card9));

        cardRepository.saveAll(List.of(card1, card2, card3, card4, card5, card6, card7, card8, card9));

    }

}
