package ru.kireev.ATM.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "card")
//@EqualsAndHashCode(exclude = "client")
@EqualsAndHashCode
@Accessors(chain = true)
@ToString
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "cardNumber")
    private int cardNumber;

    @Column(name = "pin")
    private int pin;

    @Column(name = "balance")
    private BigDecimal balance;

//    @ManyToOne()
//    private Client client;

}
