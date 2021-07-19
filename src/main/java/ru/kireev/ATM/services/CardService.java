package ru.kireev.ATM.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kireev.ATM.entities.Card;
import ru.kireev.ATM.repositories.CardRepository;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardService implements UserDetailsService {

    private final CardRepository cardRepository;


    public Card findByCardNumber(int cardNumber) {

        return cardRepository.findByCardNumber(cardNumber).orElseThrow(() -> new UsernameNotFoundException("Карты " + cardNumber + " не существует!"));

    }

    public void updateCard(Card card) {

        cardRepository.saveAndFlush(card);

    }

    public void transferMoney(Card cardFrom, Card cardTo, BigDecimal amountOfMoney) {

        cardFrom.setBalance(cardFrom.getBalance().subtract(amountOfMoney));
        cardTo.setBalance(cardTo.getBalance().add(amountOfMoney));
        cardRepository.saveAndFlush(cardFrom);
        cardRepository.saveAndFlush(cardTo);

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

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String cardNumber) throws UsernameNotFoundException {

        Card card = findByCardNumber(Integer.parseInt(cardNumber));

        return new User(String.valueOf(card.getCardNumber()), card.getPin(), card.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList()));

    }
}
