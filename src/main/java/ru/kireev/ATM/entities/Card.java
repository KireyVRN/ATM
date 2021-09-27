package ru.kireev.ATM.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;


@Entity
@Data
@Table(name = "cards")
@EqualsAndHashCode(exclude = {"operations", "roles"})
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "card_number", unique = true)
    private String cardNumber;

    @Column(name = "pin")
    private String pin;

    @Column(name = "balance")
    private BigDecimal balance;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private Client client;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cards_roles",
            joinColumns = @JoinColumn(name = "card_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @OneToMany(mappedBy = "card", fetch = FetchType.LAZY)
    private Set<Operation> operations;

    public String getLastNumbers() {

        return String.format("*%s", String.valueOf(cardNumber).substring(5));

    }

    public boolean hasEnoughMoney(BigDecimal amountOfMoney) {

        if (amountOfMoney == null) amountOfMoney = new BigDecimal(0);
        return amountOfMoney.compareTo(balance) < 1;

    }

}
