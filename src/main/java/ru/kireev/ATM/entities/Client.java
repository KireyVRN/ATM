package ru.kireev.ATM.entities;

import javax.validation.constraints.*;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@Data
@Table(name = "clients")
@EqualsAndHashCode(exclude = "cards")
@Accessors(chain = true)
@ToString(exclude = "cards")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

   // @NotBlank
    @Column(name = "name")
    private String name;

   // @NotBlank
    @Column(name = "surname")
    private String surname;

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private Set<Card> cards;

    public Client addCard(Card card) {

        if (cards == null) cards = new HashSet<>();

        this.cards.add(card);
        card.setClient(this);
        return this;

    }

}
