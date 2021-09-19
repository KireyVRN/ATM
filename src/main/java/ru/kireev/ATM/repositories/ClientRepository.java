package ru.kireev.ATM.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kireev.ATM.entities.Client;


@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
}
