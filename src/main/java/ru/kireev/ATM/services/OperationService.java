package ru.kireev.ATM.services;

import ru.kireev.ATM.entities.Card;
import ru.kireev.ATM.entities.Operation;

import java.util.List;


public interface OperationService {

    void saveOperation(Operation operation);

    List<Operation> getLastOperationsByCard(Card card);

}
