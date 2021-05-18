"use strict";

const { cloneDeep } = require("lodash");
const faker = require("faker");

const generateLoan = async (payload, generateFake = false) => {
    if(!generateFake) {
        const clonePayload = cloneDeep(payload);

        return {
            //Use Monte-Carlo distribution to generate the loan transaction based on the total Bill amount
        };
    }

    return {
        id: faker.datatype.number(100000)
    };
};

module.exports = generateLoan;
