"use strict";

const _ = require("lodash");
const csv = require("./src/utils/csv-node-handler");

/**
 * do something with data and write to a local json
 * this method can be extended to store data in a 
 * Mongo instance, if a need arises, to maintain
 * historical simulation data set.
*/
const transformAndStore = data => data;

module.exports = async function generateTransactionDataset (csv_file_path) {

    try {
        const users_default_dataset = await csv.read(csv_file_path);
        _.forEach(users_default_dataset, (user_data)=>{
            console.log(user_data);
            transformAndStore(user_data);
        }
        );
    } catch (error) {
        console.error(error);
    }
};
