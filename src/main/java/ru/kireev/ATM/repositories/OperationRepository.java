package ru.kireev.ATM.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kireev.ATM.entities.Card;
import ru.kireev.ATM.entities.Operation;

import java.util.List;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {

    //@Query("SELECT o FROM Operation o WHERE o.card.id = :cardId ORDER BY o.dateAndTime DESC LIMIT 10")
    List<Operation> findTop10ByCardOrderByDateAndTimeDesc(Card card);

}
