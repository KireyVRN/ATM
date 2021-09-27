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

    @Enumerated(value = EnumType.STRING)
    @Column(name = "operation_type")
    private OperationType operationType;

    @NotNull(message = "Укажите сумму")
    @Column(name = "amount_of_money")
    @Digits(integer = 11, fraction = 2, message = "Некорректная сумма")
    @DecimalMin(value = "0.01", message = "Сумма должна быть не менее 0.01")
    @DecimalMax(value = "1000000000", message = "Максимальная сумма операции - 1 000 000 000")
    private BigDecimal amountOfMoney;

    @Column(name = "from_card")
    private String fromCardNumber;

    @Column(name = "to_card")
    @Pattern(regexp = "\\d{8}", message = "Номер карты должен быть восьмизначным числом")
    private String toCardNumber;

    @Column(name = "date_and_time")
    private LocalDateTime dateAndTime;

    @ManyToOne
    @JoinColumn(name = "card_id", referencedColumnName = "id")
    private Card card;

    public String getInformationForHistory() {

        String information = null;

        switch (operationType) {

            case DEPOSIT -> information = String.format("%s Пополнение баланса %s", dateAndTime.format(DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy")), amountOfMoney);
            case WITHDRAWAL -> information = String.format("%s Вывод средств %s", dateAndTime.format(DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy")), amountOfMoney);
            case TRANSFER -> {

                if (card.getCardNumber().equals(fromCardNumber)) {
                    information = String.format("%s Перевод %s на %s", dateAndTime.format(DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy")), amountOfMoney, toCardNumber);
                } else {
                    information = String.format("%s Перевод %s от %s", dateAndTime.format(DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy")), amountOfMoney, fromCardNumber);
                }

            }
        }

        return information;

    }

}
