'use strict';

const axios = require('axios');
const config = require('config');
const { startOfYear, startOfToday } = require('date-fns');
const { Constants, Files, Logger } = require('./src/utils/');
const { getStartDate, resolveConfigData } = require('./src/utils/resolvers');
const streamFileAndGenerateData = require('./src/stream-and-generate');
const { HTTP_METHODS, LOG_FOLDER, LOG_FILE } = Constants;

const merchants = require('./data/source/Merchant.json');
const consumers = require('./data/output/consumers.json');
const loans = require('./data/output/loans.json');

const START_DATE = getStartDate();

Files.createDirectory(LOG_FOLDER);
const logger = new Logger(LOG_FILE, false);

// const { bindAllHandlers } = require('./src/event-handlers');
// bindAllHandlers(process);

//TODO: pass this filename as a parameter from Docker run command
const csvFilePath  = './data/source/UCI_Credit_Card.100.csv';

/**
 * Take a source dataset file as input and generates transactions based on the config provided
 * @param {String} file    - path of the file with source dataset
 * @param {Object} config  - configuration object provided to txn generation process
 */
const generateTransactionsFromFile = async (file) => {
    logger.info(`Starting the transaction generation process using file: ${file}`);

    try {
        await streamFileAndGenerateData(file);
        logger.info('Process complete!');
    } catch (error) {
        logger.error(`Error in generating transactions: ${error}`);
    }
};

const setEngineTime = async time => {
    logger.info('Setting server time...');
    const options = {
        url: `${config.get('trancheCompiler').url}/set/time`,
        method: HTTP_METHODS.POST,
        headers: { 'content-type': 'text/json'},
        data: START_DATE
    };

    await axios(options);
    logger.info('Server time set successfully!');
};

const setTrancheEngineConfig = async date => {
    logger.info('Setting server config...');
    const options = {
        url: `${config.get('trancheCompiler').url}/set/config`,
        method: HTTP_METHODS.POST,
        data: resolveConfigData(date)
    };

    await axios(options);
    logger.info('Server config set successfully!');
};

const sendMerchants = async merchants => {
    logger.info('Sending merchant records...');
    const options = {
        url: `${config.get('trancheCompiler').url}/merchants/${id}`,
        method: HTTP_METHODS.POST,
    };

    for(const merchant of merchants) {
        options.data = merchant;
        await axios(options);
    }

    logger.info('Merchant records sent successfully!');
};

const sendConsumers = async consumers => {
    logger.info('Sending consumer records...');
    const options = {
        url: `${config.get('trancheCompiler').url}/consumers/${id}`,
        method: HTTP_METHODS.POST,
    };

    for(const consumer of consumers) {
        options.data = consumer;
        await axios(options);
    }

    logger.info('Consumer records sent successfully!');
};

const sendLoans = async loans => {
    logger.info('Sending loan records...');
    const options = {
        url: `${config.get('trancheCompiler').url}/loans/${id}`,
        method: HTTP_METHODS.POST,
        data: 'date'
    };

    for(const loan of loans) {
        options.data = loan;
        await axios(options);
    }
    logger.info('Loan records sent successfully!');
};

const runSimulation = async () => {
    logger.info('Starting simulation...');
    
    await generateTransactionsFromFile(csvFilePath);
    await setEngineTime(START_DATE);
    await setTrancheEngineConfig(START_DATE);
    await sendMerchants(merchants);
    await sendConsumers(consumers);
    await sendLoans(loans);

    logger.info('Starting completed!');
};

runSimulation();
