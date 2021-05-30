'use strict';

const axios = require('axios');
const config = require('config');
const { startOfYear, startOfToday } = require('date-fns');
const { Constants, Files, Logger } = require('./src/utils/');
const { getStartDate, resolveConfigData } = require('./src/utils/resolvers');
const streamFileAndGenerateData = require('./src/stream-and-generate');
const { HTTP_METHODS, LOG_FOLDER, LOG_FILE } = Constants;

const START_DATE = getStartDate();
const SLEEP_TIMER = 2;

const sleep = ms => new Promise(res => setTimeout(res, ms))

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
    await sleep(2000);
    console.log(`Starting the transaction generation process using file: ${file}`);
    await sleep(2000);
    try {
        await streamFileAndGenerateData(file);
        console.log('Process complete!');
    } catch (error) {
        logger.error(`Error in generating transactions: ${error}`);
    }
};

const setEngineTime = async time => {
    await sleep(2000);
    console.log('Setting server time...');
    await sleep(2000);
    const options = {
        url: `${config.get('trancheCompiler').url}/set/time`,
        method: HTTP_METHODS.POST,
        headers: { 'content-type': 'text/json'},
        data: START_DATE
    };

    // await axios(options);
    console.log('Server time set successfully!');
};

const setTrancheEngineConfig = async date => {
    await sleep(2000);
    console.log('Setting server config...');
    await sleep(2000);
    const options = {
        url: `${config.get('trancheCompiler').url}/set/config`,
        method: HTTP_METHODS.POST,
        data: resolveConfigData(date)
    };

    const result = await axios(options);
    console.log(result.data);
    console.log('Server config set successfully!');
};

const sendMerchants = async () => {
    const merchants = require('./data/source/Merchant.json');
    await sleep(2000);
    console.log('Sending merchant records...');
    await sleep(2000);

    // const options = {
    //     url: `${config.get('trancheCompiler').url}/merchants/${merchants[0].id}`,
    //     method: HTTP_METHODS.POST,
    //     data: merchants[0]
    // };

    for(const merchant of merchants) {
        const options = {
            url: `${config.get('trancheCompiler').url}/merchants/${merchant.id}`,
            method: HTTP_METHODS.POST,
            data: merchant
        };
        const result = await axios(options);
        console.log(result.data);
        // console.log(`merchant ${merchant.id}`);
        await sleep(SLEEP_TIMER);
    }

    console.log('Merchant records sent successfully!');
};

const sendConsumers = async () => {
    const consumers = require('./data/output/consumers.json');
    await sleep(2000);
    console.log('Sending consumer records...');
    await sleep(2000);

    // const options = {
    //     url: `${config.get('trancheCompiler').url}/consumers/${consumers[0].id}`,
    //     method: HTTP_METHODS.POST,
    //     data: consumers[0]
    // };
    // const result = await axios(options);
    // console.log(result.data);

    for(const consumer of consumers) {
        const options = {
            url: `${config.get('trancheCompiler').url}/consumers/${consumer.id}`,
            method: HTTP_METHODS.POST,
            data: consumer
        };
        
        const result = await axios(options);
        console.log(result.data);
        // console.log(`consumer ${consumer.id}`);
        await sleep(SLEEP_TIMER);
    }

    console.log('Consumer records sent successfully!');
};

const sendLoans = async () => {
    const loans = require('./data/output/loans.json');
    await sleep(2000);
    console.log('Sending loan records...');
    await sleep(2000);

    // const options = {
    //         url: `${config.get('trancheCompiler').url}/loans/${loans[0].id}`,
    //         method: HTTP_METHODS.POST,
    //         data: loans[0]
    //     };
    
    // const result = await axios(options);
    // console.log(result.data);

    for(const loan of loans) {
        const options = {
            url: `${config.get('trancheCompiler').url}/loans/${loan.id}`,
            method: HTTP_METHODS.POST,
            data: loan
        };
        
        const result = await axios(options);
        console.log(result.data);
        // console.log(`loan ${loan.timestamp} ${loan.id} ${loan.consumerId} ${loan.amount}`);
        await sleep(SLEEP_TIMER);
    }

    console.log('Loan records sent successfully!');
};

const runSimulation = async () => {
    console.log('Starting simulation...');
    await generateTransactionsFromFile(csvFilePath);
    // await setEngineTime(START_DATE);
    await setTrancheEngineConfig(START_DATE);
    await sendMerchants();
    await sendConsumers();
    await sendLoans();

    console.log('Simulation successfully completed!');
};

runSimulation();
