package com.mirafintech.prototype.dto;


public record ConsumerDto(Long id,
                          Integer limitBalance, // Amount of given credit in NT dollars (includes individual and family/supplementary credit
                          Integer education, // (1=graduate school, 2=university, 3=high school, 4=others, 5=unknown, 6=unknown)
                          Integer sex, // (1=male, 2=female)
                          Integer martialStatus, // (1=married, 2=single, 3=others)
                          Integer age, // int years
                          Integer initialCreditScore) {} // initial credit score for the user: 0-100 (inclusive)



