"use strict";

const { cloneDeep } = require("lodash");
const faker = require("faker");

const generateLoan = async (payload, generateFake = false) => {
    if(!generateFake) {
        const clonePayload = cloneDeep(payload);

        return {
            //do something
        };
    }

    return {
        id: faker.datatype.number(100000)
    };
};

module.exports = generateLoan;
