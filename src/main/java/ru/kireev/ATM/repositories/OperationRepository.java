package ru.kireev.ATM.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.kireev.ATM.entities.Operation;

@Repository
//@Transactional
public interface OperationRepository extends JpaRepository<Operation, Long> {
}
