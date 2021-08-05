package ru.kireev.ATM.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;


@Entity
@Data
@Table(name = "cards")
@EqualsAndHashCode(exclude = {"operations", "roles"})
@Accessors(chain = true)
@ToString(exclude = {"operations", "roles"})
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "pin")
    private String pin;

    @Column(name = "balance")
    private BigDecimal balance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private Client client;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "cards_roles",
            joinColumns = @JoinColumn(name = "card_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @OneToMany(mappedBy = "card", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Operation> operations;

    public Card addOperation(Operation operation) {

        if (operations == null) operations = new ArrayList<>();

        this.operations.add(operation);
        operation.setCard(this);
        return this;

    }

    public String getLastNumbers() {

        return String.format("*%s", String.valueOf(cardNumber).substring(5));

    }

    public boolean hasEnoughMoney(BigDecimal amountOfMoney) {

        return amountOfMoney.compareTo(balance) < 1;

    }

}
