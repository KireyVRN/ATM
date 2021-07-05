package ru.kireev.ATM.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@Table(name = "client")
@EqualsAndHashCode(exclude = "cards")
//@EqualsAndHashCode
@Accessors(chain = true)
@ToString
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

//    @OneToMany(mappedBy = "client")
//    private Set<Card> cards;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Set<Card> cards;

}
