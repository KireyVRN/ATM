package ru.kireev.ATM.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kireev.ATM.entities.Card;
import ru.kireev.ATM.entities.Client;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
//@Transactional
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByName(String clientName);

}
