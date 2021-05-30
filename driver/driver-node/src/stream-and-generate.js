'use strict';

const fs = require('fs');
const path = require('path');
const _ = require('lodash');
const es = require('event-stream');
const csvtojson = require('csvtojson');
const { getTransactionDates, resolveConsumer, resolveConsumerLoan, resolveConsumerRisk } = require('./utils/resolvers');

const streamFileAndGenerateData = file => {
    let totalRecordsProcessed = 0;
    const consumers = [];
    const consumerLoans = [];
    //TODO: Payments - need to merge with loans file with a transaction type
    // const payments = [];

    //Date distribution setup
    const dateArray = getTransactionDates();

    const stream = fs
                    .createReadStream(file)
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
                        // consumerRisks.push(risk);  //debugging only
                    })
                    .on('error', function(err) {
                        console.log('Error while reading file.', err);
                    })
                    .on('end', function() {
                        const writeConsumer = fs.createWriteStream(`./data/output/consumers.json`);
                        const writeConsumerLoans = fs.createWriteStream(`./data/output/loans.json`);
                        // const writeRisk = fs.createWriteStream(`./data/output/${path.basename(file, path.extname(path.resolve(file)))+'_risk.json'}`);  //debugging only

                        consumerLoans.sort((a, b) => a.timestamp - b.timestamp);

                        writeConsumer.write(JSON.stringify(consumers, undefined, 2));
                        writeConsumerLoans.write(JSON.stringify(consumerLoans, undefined, 2));
                        // writeRisk.write(JSON.stringify(consumerRisks, undefined, 2));  //debugging only

                        console.log(`Total records processed: ${totalRecordsProcessed}`);
                        console.log(`Total consumers added: ${consumers.length}`);
                        console.log(`Total loans added: ${consumerLoans.length}`);
                    })
                )
};

// streamFileAndGenerateData('../data/source/UCI_Credit_Card.100.csv');
module.exports = streamFileAndGenerateData;
