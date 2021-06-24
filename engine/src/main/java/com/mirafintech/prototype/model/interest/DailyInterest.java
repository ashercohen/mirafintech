package com.mirafintech.prototype.model.interest;


public sealed interface DailyInterest extends Interest
        permits DailyInterest360, DailyInterest365 {}

