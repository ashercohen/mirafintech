'use strict';

const fs = require('fs');
const es = require('event-stream');
const csvtojson = require('csvtojson');
const {
    resolveConsumer,
    resolveConsumerLoan,
    resolveConsumerRisk,
    resolveConsumerPayment,
    resolveConsumerOutputFileName,
    resolveTransactionOutputFileName,
} = require('./utils/resolvers');

const streamFileAndGenerateData = filePath => {
    let totalRecordsProcessed = 0;
    const consumers = [];
    const consumerTransactions = [];

    return new Promise((resolve, reject) => {
        const stream = fs.createReadStream(filePath);
        stream
            .pipe(csvtojson({
                downstreamFormat: 'line',
                checkType: true
            }))
            .pipe(es.parse())
            .pipe(es.mapSync(function (obj) {
                totalRecordsProcessed++;

                const risk = resolveConsumerRisk(obj);
                const consumer = resolveConsumer(obj, risk);
                const loan = resolveConsumerLoan(obj);
                // const payment = resolveConsumerPayment(obj);

                consumers.push(consumer);
                consumerTransactions.push(...loan);
                // consumerTransactions.push(...payment);
            })
                .on('error', function (err) {
                    console.log('Error while reading file.', err);
                    reject();
                })
                .on('end', function () {
                    const writeConsumer = fs.createWriteStream(`./data/output/${resolveConsumerOutputFileName(filePath)}`);
                    const writeConsumerTransactions = fs.createWriteStream(`./data/output/${resolveTransactionOutputFileName(filePath)}`);
                    consumerTransactions.sort((a, b) => Date.parse(a.timestamp) - Date.parse(b.timestamp));

                    writeConsumer.write(JSON.stringify(consumers, undefined, 2));
                    writeConsumerTransactions.write(JSON.stringify(consumerTransactions, undefined, 2));

                    console.log(`Total records processed: ${totalRecordsProcessed}`);
                    console.log(`Total consumers added: ${consumers.length}`);
                    console.log(`Total transactions added: ${consumerTransactions.length}`);
                    console.log('Generation process complete!');

                    resolve();
                })
            );
    });
};

// streamFileAndGenerateData('../data/source/UCI_Credit_Card.100.csv');
module.exports = streamFileAndGenerateData;
