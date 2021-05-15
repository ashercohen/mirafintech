const moment = require("moment");
const csv = require("./utils/csv-node-handler");
const generateTransactionDataset = require("./lib/generate-transactions-dataset");
//const async = require("async");
const _ = require("lodash");
// const axios_request = require("./utils/axios-instance");



const csv_file_path  = "../../datasets/UCI_Credit_Card.100.csv";


// const first_date = moment().set("year", 2021).set("month", 1).set("date", 1);
// const last_date = moment().set("year", 2021).set("month", 6).set("date", 31);

async function fetchDataFromCsv(csvFilePath) {
    return csv.read(csvFilePath);
}


(async () => {
    try {
        const users = await fetchDataFromCsv(csv_file_path);
        _.forEach(users, (user)=>{
            generateTransactionDataset(user);
        }
        );
    } catch (error) {
        console.error(error);
    }
})();
