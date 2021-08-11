package ru.kireev.ATM.services;

import lombok.RequiredArgsConstructor;
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

    public Card findByCardNumber(String cardNumber) {

        return cardRepository.findByCardNumber(cardNumber).orElseThrow(() -> new UsernameNotFoundException("Карты " + cardNumber + " не существует!"));

    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void updateOrSaveCard(Card card) {

        cardRepository.saveAndFlush(card.setPin(new BCryptPasswordEncoder(12).encode(card.getPin())));

    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void transferMoney(Card cardFrom, Card cardTo, BigDecimal amountOfMoney) {

        Card from = cardRepository.getById(cardFrom.getId());
        Card to = cardRepository.getById(cardTo.getId());

        cardRepository.saveAndFlush(from.setBalance(cardFrom.getBalance().subtract(amountOfMoney)));
        cardRepository.saveAndFlush(to.setBalance(cardTo.getBalance().add(amountOfMoney)));

    }

    public void blockCard(Card card) {

        cardRepository.deleteById(card.getId());

    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void putMoneyIntoAccount(Card card, BigDecimal amountOfMoney) {

        card.setBalance(cardRepository.getById(card.getId()).getBalance().add(amountOfMoney));
        cardRepository.saveAndFlush(card);

    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void withdrawMoney(Card card, BigDecimal amountOfMoney) {

        Card c = cardRepository.getById(card.getId());

        if ((c.getBalance().compareTo(amountOfMoney) < 0)) {
            throw new RuntimeException("Недостаточно средств на карте");
        }

        cardRepository.saveAndFlush(c.setBalance(c.getBalance().subtract(amountOfMoney)));

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
