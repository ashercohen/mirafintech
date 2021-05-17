"use strict";

const { bindAllHandlers } = require("./event-handlers");
bindAllHandlers(process);

const axios = require("axios");
const generateTransactionDataset = require("./src/generate-transactions-dataset");
const { Constants, files, Logger } = require("./src/utils/");
const { HTTP_METHODS, LOG_FOLDER, GENERATOR_LOG_FILE } = Constants;

files.createDirectory(LOG_FOLDER);
const logger = new Logger(GENERATOR_LOG_FILE, true);

//TODO: pass this filename as a parameter from Docker run command
const csvFilePath  = "./data/source/UCI_Credit_Card.100.csv";

const generateTransactionsFromFile = async () => {
    logger.info("Starting the transaction generation process...");

    await generateTransactionDataset(csvFilePath);

    logger.info("Process complete.");
};

const setTrancheEngineConfig = async () => {
    const options = {
        method: HTTP_METHODS.POST,
        data: await getConfig() //get from configs decorator
    };

    await axios(options);
    logger.info("Server config set successfully.");
};

const runSimulation = async () => {
    logger.info("Starting simulation...");
    //Do something to run simulation
    logger.info("Starting completed!");
};

const run = async () => {
    await generateTransactionsFromFile();
    await setTrancheEngineConfig();
    await runSimulation();
};

run();
