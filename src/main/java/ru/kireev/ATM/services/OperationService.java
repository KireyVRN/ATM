package ru.kireev.ATM.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kireev.ATM.entities.Operation;
import ru.kireev.ATM.repositories.OperationRepository;

@Service
@RequiredArgsConstructor
public class OperationService {

    private final OperationRepository operationRepository;

    public void saveOperation(Operation operation) {

        operationRepository.saveAndFlush(operation);

    }
}
