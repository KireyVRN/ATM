package ru.kireev.ATM.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Set;


@Entity
@Data
@Table(name = "clients")
@EqualsAndHashCode(exclude = "cards")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER)
    private Set<Card> cards;

}
