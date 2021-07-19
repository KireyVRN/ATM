package ru.kireev.ATM.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.IndexColumn;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data()
@Table(name = "clients")
@EqualsAndHashCode(exclude = {"cards", "roles"})
@Accessors(chain = true)
@ToString(exclude = {"cards", "roles"})
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private Set<Card> cards;

//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(name = "clients_roles",
//            joinColumns = @JoinColumn(name = "client_id"),
//            inverseJoinColumns = @JoinColumn(name = "role_id"))
//    private Collection<Role> roles;

    public Client addCard(Card card) {

        if (cards == null) cards = new HashSet<>();

        this.cards.add(card);
        card.setClient(this);
        return this;

    }

}
