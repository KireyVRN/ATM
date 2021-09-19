package ru.kireev.ATM.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kireev.ATM.entities.Card;

import java.math.BigDecimal;


public interface CardService extends UserDetailsService {

    Card getCardByNumber(String cardNumber);

    void changePin(Card card);

    void transferMoney(Card cardFrom, String cardToNumber, BigDecimal amountOfMoney);

    void blockCard(Card card);

    void putMoneyIntoAccount(Card card, BigDecimal amountOfMoney);

    void withdrawMoney(Card card, BigDecimal amountOfMoney);

}
