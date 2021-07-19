package ru.kireev.ATM.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.kireev.ATM.entities.Card;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
//@Transactional
public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByCardNumber(int cardNumber);

}
