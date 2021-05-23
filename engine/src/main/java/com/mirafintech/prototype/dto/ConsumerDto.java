package com.mirafintech.prototype.dto;

import lombok.Data;


@Data
public class ConsumerDto {

    private Long id;

    private Integer limitBalance; // Amount of given credit in NT dollars (includes individual and family/supplementary credit

    private Integer education; // (1=graduate school, 2=university, 3=high school, 4=others, 5=unknown, 6=unknown)

    private Integer sex; // (1=male, 2=female)

    private Integer martialStatus; // (1=married, 2=single, 3=others)

    private Integer age; // int years

    private Integer initialCreditScore; // initial credit score for the user: 0-100 (inclusive)
}
