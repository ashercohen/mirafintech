"use strict";

const { cloneDeep } = require("lodash");

const generatePayment = async (payload, generateFake = false) => {
    if(!generateFake) {

        return {
            //do something
        };
    }

    return {
        //do something else for fake
    };
};

module.exports = generatePayment;
