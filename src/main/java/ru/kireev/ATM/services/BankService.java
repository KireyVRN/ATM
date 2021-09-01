package ru.kireev.ATM.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kireev.ATM.entities.*;

import ru.kireev.ATM.repositories.CardRepository;
import ru.kireev.ATM.repositories.ClientRepository;
import ru.kireev.ATM.repositories.OperationRepository;
import ru.kireev.ATM.repositories.RoleRepository;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class BankService {

    private final CardRepository cardRepository;
    private final ClientRepository clientRepository;
    private final OperationRepository operationRepository;
    private final RoleRepository roleRepository;


    @PostConstruct
    private void init() {

        Role card = roleRepository.save(new Role().setName("CARD"));
        // Operation operation1 = operationRepository.save(new Operation().setOperationType(OperationType.DEPOSIT));
        Card card1 = cardRepository.save(new Card().setCardNumber("11111111").setBalance(BigDecimal.valueOf(300000.34)).setPin("$2y$12$mYDzTv7h7DygQDYRas7Nd.JvyFuFjIYzOKYDPQPWIho5yGFhzHgRq").setRoles(Set.of(card)));
        Card card2 = cardRepository.save(new Card().setCardNumber("22222222").setBalance(BigDecimal.valueOf(450000.00)).setPin("$2y$12$fZhklvguWXMcY50lUbxg3eC4.Fn7jUxhEv7gbzWYQTE4.QoAY0.Ca").setRoles(Set.of(card)));
        Card card3 = cardRepository.save(new Card().setCardNumber("33333333").setBalance(BigDecimal.valueOf(110000.56)).setPin("$2y$12$R2iMn7yRFT/0ZgvOl1FZteeR1UombzN2Ow6IFVhH20fBlI/lZ5uGO").setRoles(Set.of(card)));
        Card card4 = cardRepository.save(new Card().setCardNumber("44444444").setBalance(BigDecimal.valueOf(666666.32)).setPin("$2y$12$V.8siaWtgMr6E.c.Msc2K.ff2puYDXiqeqTpu1JPObGAm49EZYKym").setRoles(Set.of(card)));
        Card card5 = cardRepository.save(new Card().setCardNumber("55555555").setBalance(BigDecimal.valueOf(7300000.00)).setPin("$2y$12$Fz7eVV426ERfrHuiBBA6eOQoFCgRgc8AhvBB8fMWFEZC5fITFQ2oC").setRoles(Set.of(card)));
        Card card6 = cardRepository.save(new Card().setCardNumber("66666666").setBalance(BigDecimal.valueOf(777300000.98)).setPin("$2y$12$2R6OIZ.O9Jrib4howVUDxOceosc0yLxtrwwPmRtVAuR8MiFjUns8G").setRoles(Set.of(card)));
        Card card7 = cardRepository.save(new Card().setCardNumber("77777777").setBalance(BigDecimal.valueOf(300000.77)).setPin("$2y$12$gjVEaV.zcd2viNe6aALg7OrBD.Vk3qwUxJJ4f0sIJT39mFsYC5k6u").setRoles(Set.of(card)));
        Card card8 = cardRepository.save(new Card().setCardNumber("88888888").setBalance(BigDecimal.valueOf(7000.54)).setPin("$2y$12$B9f1YR2fanbIwe5UN23cyeOQLRJIHGNvIwgUElb/cANN5hN0z.xYG").setRoles(Set.of(card)));
        Card card9 = cardRepository.save(new Card().setCardNumber("99999999").setBalance(BigDecimal.valueOf(4568.22)).setPin("$2y$12$iOGE13n.AS1p1rDoU3XUa.OnH1NTkq3eCMz1Wy8VR3mYZAaxnY62.").setRoles(Set.of(card)));
        // operationRepository.save(operation1);
        clientRepository.save(new Client().setName("Василий").setSurname("Ахметович").addCard(card1).addCard(card2));
        clientRepository.save(new Client().setName("Мария").setSurname("Ивановна").addCard(card3).addCard(card4));
        clientRepository.save(new Client().setName("Jhon").setSurname("Blackberry").addCard(card5).addCard(card6).addCard(card7).addCard(card8));
        clientRepository.save(new Client().setName("Яша").setSurname("Дикий").addCard(card9));

        cardRepository.saveAll(List.of(card1, card2, card3, card4, card5, card6, card7, card8, card9));

    }

}
