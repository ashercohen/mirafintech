'use strict';

const fs = require('fs');
const es = require('event-stream');
const csvtojson = require('csvtojson');
const { 
    resolveConsumer, 
    getTransactionDates, 
    resolveConsumerLoan, 
    resolveConsumerRisk,
    resolveLoanOutputFileName,
    resolveConsumerOutputFileName
} = require('./utils/resolvers');

const streamFileAndGenerateData = filePath => {
    let totalRecordsProcessed = 0;
    const consumers = [];
    const consumerLoans = [];
    //TODO: Payments - need to merge with loans file with a transaction type
    // const payments = [];

    //Date distribution setup for transactions
    const dateArray = getTransactionDates();

    const stream = fs
        .createReadStream(filePath)
        .pipe(csvtojson({
            downstreamFormat: 'line',
            checkType: true
        }))
        .pipe(es.parse())
        .pipe(es.mapSync(function(obj) {
            totalRecordsProcessed++;

            const risk = resolveConsumerRisk(obj);
            const consumer = resolveConsumer(obj, risk);
            const loan = resolveConsumerLoan(obj, dateArray);
            
            consumers.push(consumer);
            consumerLoans.push(...loan);
        })
        .on('error', function(err) {
            console.log('Error while reading file.', err);
        })
        .on('end', function() {
            const writeConsumer = fs.createWriteStream(`./data/output/${resolveConsumerOutputFileName(filePath)}`);
            const writeConsumerLoans = fs.createWriteStream(`./data/output/${resolveLoanOutputFileName(filePath)}`);

            consumerLoans.sort((a, b) => a.timestamp - b.timestamp);

            writeConsumer.write(JSON.stringify(consumers, undefined, 2));
            writeConsumerLoans.write(JSON.stringify(consumerLoans, undefined, 2));

            console.log(`Total records processed: ${totalRecordsProcessed}`);
            console.log(`Total consumers added: ${consumers.length}`);
            console.log(`Total loans added: ${consumerLoans.length}`);
            console.log('Generation process complete!');
        })
    )
};

// streamFileAndGenerateData('../data/source/UCI_Credit_Card.100.csv');
module.exports = streamFileAndGenerateData;
