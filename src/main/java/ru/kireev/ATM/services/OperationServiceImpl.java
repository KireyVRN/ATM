package ru.kireev.ATM.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kireev.ATM.entities.Card;
import ru.kireev.ATM.entities.Operation;
import ru.kireev.ATM.repositories.OperationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationServiceImpl implements OperationService {

    private final OperationRepository operationRepository;

    public void saveOperation(Operation operation) {

        operationRepository.saveAndFlush(operation);

    }

    public List<Operation> getLastOperationsByCard(Card card) {

        return operationRepository.findTop10ByCardOrderByDateAndTimeDesc(card);

    }


}
