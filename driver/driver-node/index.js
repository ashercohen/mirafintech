'use strict';

const axios = require('axios');
const config = require('config');
const args = require('minimist')(process.argv.slice(2), {
    string: true,
    alias: {
        f: 'filepath'
    }
});
const { Constants, Files, Logger } = require('./src/utils/');
const { HTTP_METHODS, LOG_FOLDER, LOG_FILE } = Constants;
const streamFileAndGenerateData = require('./src/stream-and-generate');
const { 
    getServerStartDate, 
    resolveConfigData,
    resolveConsumerOutputFileName,
    resolveTransactionOutputFileName
} = require('./src/utils/resolvers');

const START_DATE = getServerStartDate();
const USER_WAIT = 2000;

const sleep = ms => new Promise(res => setTimeout(res, ms));

Files.createDirectory(LOG_FOLDER);
const logger = new Logger(LOG_FILE, false);

// const { bindAllHandlers } = require('./src/event-handlers');
// bindAllHandlers(process);

const csvFilePath  = args['filepath'];
console.log(csvFilePath);

/**
 * Take a source dataset file as input and generates transactions based on the config provided
 * @param {String} file    - path of the file with source dataset
 * @param {Object} config  - configuration object provided to txn generation process
 */
const generateTransactionsFromFile = async (file) => {
    console.log(`Starting the transaction generation process using file: ${file}`);
    try {
        await streamFileAndGenerateData(file);
    } catch (error) {
        logger.error(`Error in generating transactions: ${error}`);
    }
};

/**
 * Set the starting date for the transactions simulation
 * @param {String} date  - logical start date for the txn simulation process
 */
const setEngineTime = async time => {
    await sleep(USER_WAIT);
    console.log('Setting server time...');
    await sleep(USER_WAIT);
    const options = {
        url: `${config.get('trancheCompiler').url}/set/time`,
        method: HTTP_METHODS.POST,
        headers: { 'content-type': 'text/json'},
        data: START_DATE
    };

    try {
        await axios(options);
    } catch (error) {
        logger.error(`Error in setting the Engine Time: ${JSON.stringify(options.data)} Error: ${error}`);
    }
    
    console.log('Server time set successfully!');
};

/**
 * Set the init configuration for the Tranche Engine as specified by the DTO object
 * @param {String} date  - logical start date for the txn simulation process
 */
const setTrancheEngineConfig = async date => {
    await sleep(USER_WAIT);
    console.log('Setting server config...');
    await sleep(USER_WAIT);
    const options = {
        url: `${config.get('trancheCompiler').url}/set/config`,
        method: HTTP_METHODS.POST,
        data: resolveConfigData(date)
    };

    try {
        const result = await axios(options);
        console.log(result.data);
    } catch (error) {
        logger.error(`Error in setting the Engine Configuration: ${JSON.stringify(options.data)} Error: ${error}`);
    }
    
    console.log('Server config set successfully!');
};

/**
 * Use the Merchants.json file and POST the records to the Tranche engine
 * Merchant.json file is expected to be under "wdir/data/source/" folder
 */
const sendMerchants = async () => {
    const merchants = require('./data/source/Merchant.json');
    await sleep(USER_WAIT);
    
    console.log('Sending merchant records...');
    await sleep(USER_WAIT);

    for(const merchant of merchants) {
        const options = {
            url: `${config.get('trancheCompiler').url}/merchants/${merchant.id}`,
            method: HTTP_METHODS.POST,
            data: merchant
        };
        
        try {
            const result = await axios(options);
            console.log(result.data);
        } catch (error) {
            logger.error(`Error in sending the Merchant: ${JSON.stringify(options.data)} Error: ${error}`);
        }
    }

    console.log('Merchant records sent successfully!');
};

/**
 * Use the Consumers.json file and POST the records to the Tranche engine.
 * Consumers.json file is added to the "wdir/data/output/" folder after
 * the processing the input file records.
 */
const sendConsumers = async () => {
    const consumers = require(`./data/output/${resolveConsumerOutputFileName(csvFilePath)}`);
    await sleep(USER_WAIT);
    console.log('Sending consumer records...');
    await sleep(USER_WAIT);

    for(const consumer of consumers) {
        const options = {
            url: `${config.get('trancheCompiler').url}/consumers/${consumer.id}`,
            method: HTTP_METHODS.POST,
            data: consumer
        };
        try {
            const result = await axios(options);
            console.log(result.data);
        } catch (error) {
            logger.error(`Error in setting the Consumer: ${JSON.stringify(options.data)} Error: ${error}`);
        }
    }

    console.log('Consumer records sent successfully!');
};

/**
 * Use the _transactions.json file and POST the records to the Tranche engine.
 * _transactions.json file is added to the "wdir/data/output/" folder after
 * the processing the input file records.
 */
const sendTransactions = async () => {
    const transactions = require(`./data/output/${resolveTransactionOutputFileName(csvFilePath)}`);
    await sleep(USER_WAIT);
    console.log('Sending transaction records...');
    await sleep(USER_WAIT);

    for(const transaction of transactions) {
        const url = `${config.get('trancheCompiler').url}/${transaction.type}/${transaction.id}`;
        delete transaction.type;
        const options = {
            url,
            method: HTTP_METHODS.POST,
            data: transaction
        };
        
        try {
            const result = await axios(options);
            console.log(result.data);
        } catch (error) {
            logger.error(`Error in setting the Transaction: ${JSON.stringify(options.data)} Error: ${error}`);
        }
    }

    console.log('Transaction records sent successfully!');
};

/**
 * Runs the simulation process, starting with the transformation
 * of the input file, followed by txns posted to the Tranche Engine.
 */
const runSimulation = async csvFilePath => {
    console.log('Starting simulation...');

    await generateTransactionsFromFile(csvFilePath);
    await setTrancheEngineConfig(START_DATE);
    await sendMerchants();
    await sendConsumers();
    await sendTransactions();

    await console.log('Simulation successfully completed!');
};

runSimulation(csvFilePath);