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
@Table(name = "operations")
@Accessors(chain = true)
@ToString
@EqualsAndHashCode
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "operation_type")
    //@Enumerated(value = EnumType.STRING)
    private OperationType operationType;

    @Column(name = "amount_of_money")
    private String amountOfMoney;

    @Column(name = "from_card")
    private int fromCard;

    @Column(name = "to_card")
    private int toCard;

    @Column(name = "date_and_time")
    private LocalDateTime dateAndTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "card_id", referencedColumnName = "id")
    private Card card;

    public String getInformationForHistory() {

        String information = null;

        switch (operationType) {
            case DEPOSIT -> information = String.format("Пополнение баланса %s руб. %s", amountOfMoney, dateAndTime);
            case WITHDRAWAL -> information = String.format("Вывод средств %s руб. %s", amountOfMoney, dateAndTime);
            case TRANSFER -> {
                System.out.println("КАРТА" + card.getCardNumber() + " FROM " + fromCard + " TO " + toCard);
                if (card.getCardNumber() == fromCard) {
                    information = String.format("Перевод %s руб. на %s %s", amountOfMoney, toCard, dateAndTime);
                } else {
                    information = String.format("Перевод %s руб. от %s %s", amountOfMoney, fromCard, dateAndTime);
                }
            }
            case PIN_CHANGE -> information = String.format("Изменение пин-кода  %s", dateAndTime);
        }

        return information;

    }

}
