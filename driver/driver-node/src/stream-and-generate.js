'use strict';

const fs = require('fs');
const path = require('path');
const es = require('event-stream');
const csvtojson = require('csvtojson');
const { resolveConsumer, resolveConsumerLoan, resolveConsumerRisk } = require('./utils/resolvers');

const streamFileAndGenerateData = file => {
    let totalRecordsProcessed = 0;
    const consumers = [];
    const consumerLoans = [];
    //TODO: Payments - need to merge with loans file with a transaction type
    // const payments = [];
    // const consumerRisks = []; //not needed, only for debugging purpose

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
                        const loan = resolveConsumerLoan(obj);
                        
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

                        writeConsumer.write(JSON.stringify(consumers, undefined, 2));
                        writeConsumerLoans.write(JSON.stringify(consumerLoans, undefined, 2));
                        // writeRisk.write(JSON.stringify(consumerRisks, undefined, 2));  //debugging only

                        console.log(`Total records processed: ${totalRecordsProcessed}`);
                    })
                )
};

// streamFileAndGenerateData('../data/source/UCI_Credit_Card.100.csv');
module.exports = streamFileAndGenerateData;
