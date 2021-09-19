//package ru.kireev.ATM.services;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import ru.kireev.ATM.entities.Card;
//import ru.kireev.ATM.repositories.CardRepository;
//
//import java.math.BigDecimal;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.*;
//
//
////@ExtendWith(MockitoExtension.class)
////class CardServiceImplTest {
////
////    @InjectMocks
////    private CardServiceImpl cardService;
////
////    @Mock
////    private CardRepository cardRepository;
////
////    @Test
////    void putMoneyIntoAccount() {
////
////        Card actual = new Card().setBalance(new BigDecimal("0"));
////        Card expected = new Card().setBalance(new BigDecimal("1234"));
////
////        when(cardRepository.getById(any())).thenReturn(actual);
////        when(cardRepository.saveAndFlush(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));
////
////        cardService.putMoneyIntoAccount(actual, new BigDecimal("1234"));
////
////        assertEquals(expected, actual);
////
////    }
////
////}