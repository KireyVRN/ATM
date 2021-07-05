package ru.kireev.ATM.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Entity
@Data
@Table(name = "operation")
@Accessors(chain = true)
@ToString
@EqualsAndHashCode()
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "operationType")
    //@Enumerated(value = EnumType.STRING)
    private OperationType operationType;

    @Column(name = "amountOfMoney")
    private String amountOfMoney;

    @Column(name = "fromCard")
    private int fromCard;

    @Column(name = "toCard")
    private int toCard;

    @Column(name = "dateAndTime")
    private LocalDateTime dateAndTime;

}
