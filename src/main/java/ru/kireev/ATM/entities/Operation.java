package ru.kireev.ATM.entities;

import javax.validation.constraints.*;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Entity
@Data
@Table(name = "operations")
@Accessors(chain = true)
@ToString
@EqualsAndHashCode
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "operation_type")
    private OperationType operationType;

    @Column(name = "amount_of_money")
    @Digits(integer = 50, fraction = 2, message = "Некорректная сумма")
    @DecimalMin(value = "0.01", message = "Сумма должна быть не менее 0.01")
    private BigDecimal amountOfMoney;

    @Column(name = "from_card")
    private String fromCard;

    @Column(name = "to_card")
    @Pattern(regexp = "\\d{8}", message = "Номер карты должен быть восьмизначным числом")
    private String toCard;

    @Column(name = "date_and_time")
    private LocalDateTime dateAndTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "card_id", referencedColumnName = "id")
    private Card card;

    public String getInformationForHistory() {

        String information = null;

        switch (operationType) {
            case DEPOSIT -> information = String.format("%s Пополнение баланса %s", dateAndTime.format(DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy")), amountOfMoney);
            case WITHDRAWAL -> information = String.format("%s Вывод средств %s", dateAndTime.format(DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy")), amountOfMoney);
            case TRANSFER -> {
                if (card.getCardNumber().equals(fromCard)) {
                    information = String.format("%s Перевод %s на %s", dateAndTime.format(DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy")), amountOfMoney, toCard);
                } else {
                    information = String.format("%s Перевод %s от %s", dateAndTime.format(DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy")), amountOfMoney, fromCard);
                }
            }
            case PIN_CHANGE -> information = String.format("%s Изменение пин-кода", dateAndTime.format(DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy")));
        }

        return information;

    }

}
