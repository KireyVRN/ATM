package ru.kireev.ATM.entities;

import javax.validation.constraints.*;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Entity
@Data
@Table(name = "operations")
@Accessors(chain = true)
@EqualsAndHashCode
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @Column(name = "operation_type")
    @Enumerated(value = EnumType.STRING)
    private OperationType operationType;

    @Column(name = "amount_of_money")
    @NotNull(message = "error.enterAmount")
    @Digits(integer = 11, fraction = 2, message = "error.incorrectAmount")
    @DecimalMin(value = "0.01", message = "error.minAmount")
    @DecimalMax(value = "1000000000", message = "error.maxAmount")
    private BigDecimal amountOfMoney;

    @Column(name = "from_card")
    private String fromCardNumber;

    @Column(name = "to_card")
    @Pattern(regexp = "\\d{8}", message = "error.correctCardNumber")
    private String toCardNumber;

    @Column(name = "date_and_time")
    private LocalDateTime dateAndTime;

    @ManyToOne
    @JoinColumn(name = "card_id", referencedColumnName = "id")
    private Card card;

    public String getDateAndTime() {
        return dateAndTime.format(DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy"));
    }

}
