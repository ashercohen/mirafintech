package com.mirafintech.prototype.dto;

import lombok.Data;


@Data
public class ConsumerDto {

    private long id;

    private int limitBalance; // Amount of given credit in NT dollars (includes individual and family/supplementary credit

    private int education; // (1=graduate school, 2=university, 3=high school, 4=others, 5=unknown, 6=unknown)

    private int sex; // (1=male, 2=female)

    private int martialStatus; // (1=married, 2=single, 3=others)

    private int age; // int years

    private int creditScore; // initial credit score for the user: 0-100 (inclusive)
}
