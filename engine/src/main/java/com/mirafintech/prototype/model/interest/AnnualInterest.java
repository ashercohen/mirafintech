package com.mirafintech.prototype.model.interest;


sealed interface AnnualInterest extends Interest
        permits APR {}

