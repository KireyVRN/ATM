package ru.kireev.ATM.services;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.kireev.ATM.entities.Card;
import ru.kireev.ATM.repositories.CardRepository;

import java.math.BigDecimal;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CardService implements UserDetailsService {

    private final CardRepository cardRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(CardService.class);

    public Card findByCardNumber(String cardNumber) {

        return cardRepository.findByCardNumber(cardNumber).orElseThrow(() -> new UsernameNotFoundException("Карты " + cardNumber + " не существует!"));

    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void changePin(Card card) {

        String unencryptedPin = card.getPin();
        cardRepository.saveAndFlush(card.setPin(new BCryptPasswordEncoder(12).encode(card.getPin())));
        LOGGER.info(String.format("Card %s pin was changed on %s", card.getCardNumber(), unencryptedPin));

    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void transferMoney(Card cardFrom, Card cardTo, BigDecimal amountOfMoney) {

        Card from = cardRepository.getById(cardFrom.getId());
        Card to = cardRepository.getById(cardTo.getId());

        cardRepository.saveAndFlush(from.setBalance(cardFrom.getBalance().subtract(amountOfMoney)));
        cardRepository.saveAndFlush(to.setBalance(cardTo.getBalance().add(amountOfMoney)));

        LOGGER.info(String.format("Money %s was transferred from card %s to card %s", amountOfMoney, cardFrom.getCardNumber(), cardTo.getCardNumber()));

    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void blockCard(Card card) {

        cardRepository.deleteById(card.getId());
        LOGGER.info("Card " + card.getCardNumber() + " was blocked");

    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void putMoneyIntoAccount(Card card, BigDecimal amountOfMoney) {

        card.setBalance(cardRepository.getById(card.getId()).getBalance().add(amountOfMoney));
        cardRepository.saveAndFlush(card);
        LOGGER.info(String.format("Money %s was put on the card %s", amountOfMoney, card.getCardNumber()));

    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void withdrawMoney(Card card, BigDecimal amountOfMoney) {

        Card c = cardRepository.getById(card.getId());
        cardRepository.saveAndFlush(c.setBalance(c.getBalance().subtract(amountOfMoney)));
        LOGGER.info(String.format("Money %s was withdrawn from the card %s", amountOfMoney, card.getCardNumber()));

    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean cardExists(String cardNumber) {

        return cardRepository.findByCardNumber(cardNumber).isPresent();

    }


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String cardNumber) throws UsernameNotFoundException {

        Card card = findByCardNumber(cardNumber);

        return new User(String.valueOf(card.getCardNumber()), card.getPin(), card.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList()));

    }
}
