package ru.kireev.ATM.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kireev.ATM.entities.Card;

import java.util.Optional;


@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByCardNumber(String cardNumber);

}
