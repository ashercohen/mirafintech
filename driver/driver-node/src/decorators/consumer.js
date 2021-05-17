"use strict";

const { cloneDeep } = require("lodash");
const faker = require("faker");

const generateConsumer = async (payload, generateFake = false) => {
    if(!generateFake) {
        const clonePayload = cloneDeep(payload);

        return {
            id: clonePayload.id
        };
    }

    return {
        id: faker.datatype.number(100000)
    };
};

module.exports = generateConsumer;
