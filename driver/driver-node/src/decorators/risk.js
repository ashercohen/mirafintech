"use strict";

const { cloneDeep } = require("lodash");

const getTransactionRisk = async (payload, generateFake = false) => {
    if(!generateFake) {

        return {
            //do something
        };
    }

    return {
        //do something else for fake
    };
};

module.exports = getTransactionRisk;
