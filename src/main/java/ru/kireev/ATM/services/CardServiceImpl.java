package ru.kireev.ATM.services;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.kireev.ATM.entities.Card;
import ru.kireev.ATM.repositories.CardRepository;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(CardServiceImpl.class);

    @Transactional(readOnly = true)
    public Card getCardByNumber(String cardNumber) {

        return cardRepository
                .findByCardNumber(cardNumber)
                .orElseThrow(() -> new NoSuchElementException("Card " + cardNumber + " doesn't exist!"));

    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void transferMoney(Card cardFrom, String cardToNumber, BigDecimal amountOfMoney) throws IllegalArgumentException, NoSuchElementException {

        if (cardFrom.getCardNumber().equals(cardToNumber)) {

            LOGGER.warn(String.format("Attempt to transfer money to the same card %s", cardFrom.getCardNumber()));
            throw new IllegalArgumentException("error.currentCard");

        } else if (cardRepository.findByCardNumber(cardToNumber).isEmpty()) {

            LOGGER.warn(String.format("Attempt to transfer money(%s) from card %s to not existing card %s", amountOfMoney, cardFrom.getCardNumber(), cardToNumber));
            throw new NoSuchElementException("error.noCard");

        } else if (!cardFrom.hasEnoughMoney(amountOfMoney)) {

            LOGGER.warn(String.format("Attempt to transfer too much money(%s) from card %s", amountOfMoney, cardFrom.getCardNumber()));
            throw new IllegalArgumentException("error.notEnoughMoney");

        } else {

            cardFrom = cardRepository.getById(cardFrom.getId());
            Card cardTo = cardRepository.findByCardNumber(cardToNumber).get();

            cardFrom.setBalance(cardFrom.getBalance().subtract(amountOfMoney));
            cardTo.setBalance(cardTo.getBalance().add(amountOfMoney));

            LOGGER.info(String.format("Money %s was transferred from card %s to card %s", amountOfMoney, cardFrom.getCardNumber(), cardTo.getCardNumber()));
        }

    }

    @Transactional
    public void blockCard(Card card) {

        //удаляем ссылки у операций на карту, чтобы избежать нарушения ограничения ссылочной целостности
        cardRepository.getById(card.getId()).getOperations().stream().forEach(operation -> operation.setCard(null));
        cardRepository.deleteById(card.getId());
        LOGGER.info("Card " + card.getCardNumber() + " was blocked");

    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void putMoneyIntoAccount(Card card, BigDecimal amountOfMoney) {

        Card c = cardRepository.getById(card.getId());
        c.setBalance(c.getBalance().add(amountOfMoney));
        LOGGER.info(String.format("Money %s was put on the card %s", amountOfMoney, card.getCardNumber()));

    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void withdrawMoney(Card card, BigDecimal amountOfMoney) throws IllegalArgumentException {

        card = cardRepository.getById(card.getId());

        if (card.hasEnoughMoney(amountOfMoney)) {
            card.setBalance(card.getBalance().subtract(amountOfMoney));
            LOGGER.info(String.format("Money %s was withdrawn from card %s", amountOfMoney, card.getCardNumber()));
        } else {
            LOGGER.warn(String.format("Attempt to withdraw too much money(%s) from card %s", amountOfMoney, card.getCardNumber()));
            throw new IllegalArgumentException("error.notEnoughMoney");
        }

    }

    @Transactional
    public void changePin(Card card) throws IllegalArgumentException {

        if (!card.getPin().matches("\\d{4}")) {
            LOGGER.warn(String.format("Attempt to change PIN to an incorrect one on card %s", card.getCardNumber()));
            throw new IllegalArgumentException("error.incorrectPin");
        } else {
            cardRepository
                    .getById(card.getId())
                    .setPin(new BCryptPasswordEncoder(12).encode(card.getPin()));
        }
        LOGGER.info(String.format("Card %s PIN was changed on %s", card.getCardNumber(), card.getPin()));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String cardNumber) throws UsernameNotFoundException {

        Card authorizationCard = cardRepository.findByCardNumber(cardNumber).orElseThrow(() -> new UsernameNotFoundException("User doesn't exist"));

        return new User(String.valueOf(authorizationCard.getCardNumber()), authorizationCard.getPin(), authorizationCard.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList()));

    }
}
