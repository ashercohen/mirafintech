// const moment = require("moment");
const csv = require("./utils/csv-node-handler");
const generateTransactionDataset = require("./lib/generate-transactions-dataset");
const _ = require("lodash");

const csv_file_path  = "../../datasets/UCI_Credit_Card.100.csv";


// const first_date = moment().set("year", 2021).set("month", 1).set("date", 1);
// const last_date = moment().set("year", 2021).set("month", 6).set("date", 31);


async function run(){
    try {
        const users_default_dataset = await csv.read(csv_file_path);
        _.forEach(users_default_dataset, (user_data)=>{
          console.log(user_data);
            generateTransactionDataset(user_data);
        }
        );
    } catch (error) {
        console.error(error);
    }
}


run();
