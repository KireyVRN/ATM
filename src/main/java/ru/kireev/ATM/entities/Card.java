package ru.kireev.ATM.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "cards")
@EqualsAndHashCode(exclude = "roles")
@Accessors(chain = true)
@ToString
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "card_number")
    private int cardNumber;

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

//    @OneToMany(mappedBy = "card", fetch = FetchType.LAZY)
//    private LinkedHashSet<Operation> operations;
//
//    public Card addOperation(Operation operation) {
//
//        if (operations == null) operations = new LinkedHashSet<>();
//
//        this.operations.add(operation);
//        operation.setCard(this);
//        return this;
//
//    }

    public String getLastNumbers() {

        return "*" + String.valueOf(cardNumber).substring(5);

    }

}
