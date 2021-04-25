package com.mirafintech.prototype.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "UCI_CREDIT_CARD")
@Getter
@Setter
@ToString
@NoArgsConstructor
//@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UCICreditCard implements Serializable {

    @Id
    private Integer id;
    private Integer LIMIT_BAL;
    private Integer SEX;
    private Integer EDUCATION;
    private Integer MARRIAGE;
    private Integer AGE;
    private Integer PAY_0;
    private Integer PAY_2;
    private Integer PAY_3;
    private Integer PAY_4;
    private Integer PAY_5;
    private Integer PAY_6;
    private Integer BILL_AMT1;
    private Integer BILL_AMT2;
    private Integer BILL_AMT3;
    private Integer BILL_AMT4;
    private Integer BILL_AMT5;
    private Integer BILL_AMT6;
    private Integer PAY_AMT1;
    private Integer PAY_AMT2;
    private Integer PAY_AMT3;
    private Integer PAY_AMT4;
    private Integer PAY_AMT5;
    private Integer PAY_AMT6;
    private Integer default_payment_next_month;

    public UCICreditCard(String csv) {
        List<Integer> values = Arrays.stream(csv.split(",")).map(String::trim).map(Integer::parseInt).toList();
        this.id = values.get(0);
        this.LIMIT_BAL = values.get(1);
        this.SEX = values.get(2);
        this.EDUCATION = values.get(3);
        this.MARRIAGE = values.get(4);
        this.AGE = values.get(5);
        this.PAY_0 = values.get(6);
        this.PAY_2 = values.get(7);
        this.PAY_3 = values.get(8);
        this.PAY_4 = values.get(9);
        this.PAY_5 = values.get(10);
        this.PAY_6 = values.get(11);
        this.BILL_AMT1 = values.get(12);
        this.BILL_AMT2 = values.get(13);
        this.BILL_AMT3 = values.get(14);
        this.BILL_AMT4 = values.get(15);
        this.BILL_AMT5 = values.get(16);
        this.BILL_AMT6 = values.get(17);
        this.PAY_AMT1 = values.get(18);
        this.PAY_AMT2 = values.get(19);
        this.PAY_AMT3 = values.get(20);
        this.PAY_AMT4 = values.get(21);
        this.PAY_AMT5 = values.get(22);
        this.PAY_AMT6 = values.get(23);
        this.default_payment_next_month = values.get(24);
    }
}
