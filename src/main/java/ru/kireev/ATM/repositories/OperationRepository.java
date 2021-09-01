package ru.kireev.ATM.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kireev.ATM.entities.Operation;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {
}
