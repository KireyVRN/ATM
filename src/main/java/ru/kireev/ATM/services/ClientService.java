package ru.kireev.ATM.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.kireev.ATM.entities.Client;
import ru.kireev.ATM.repositories.ClientRepository;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

//    public Client findByClientName(String clientName) {
//
//        return clientRepository.findByName(clientName).orElseThrow(() -> new UsernameNotFoundException("Пользователя " + clientName + " не существует!"));
//
//    }
//
//    public Client getClientById(long id) {
//
//        return clientRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Такого пользователя не существует!"));
//
//    }

}
