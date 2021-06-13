'use strict';

const path = require('path');
const faker = require('faker');
const { 
    add, isDate, isAfter,
    addDays, addMonths, startOfDay,
    startOfHour, startOfYear, startOfToday,
    startOfMonth, startOfMinute, startOfSecond
} = require('date-fns');

const merchants = require('../../data/source/Merchant.json');

const ID_MAX = 2000000000;
const ID_MIN = 1000000000;
const TRANSACTION_COUNT_PER_MONTH = 5;
const DURATION_MAP = {
    seconds: startOfSecond,
    minutes: startOfMinute,
    hours: startOfHour,
    days: startOfDay,
    months: startOfMonth,
    years: startOfYear
}

const addReducer = (accumulator, currentValue) => accumulator + currentValue;

const getBillArray = obj => [obj.BILL_AMT1, obj.BILL_AMT2, obj.BILL_AMT3, obj.BILL_AMT4, obj.BILL_AMT5, obj.BILL_AMT6];
const getPaymentsArray = obj => [obj.PAY_AMT1, obj.PAY_AMT2, obj.PAY_AMT3, obj.PAY_AMT4, obj.PAY_AMT5, obj.PAY_AMT6];
const getConsumerId = obj => obj.ID;

const getStartDate = () => startOfYear(startOfToday());

const resolveFileName = file => path.basename(file, path.extname(path.resolve(file)));
const resolveConsumerOutputFileName = file => resolveFileName(file) +'_consumers.json'
const resolveLoanOutputFileName = file => resolveFileName(file) +'_loans.json'

/**
 * Get a custom date range array based on beloe params
 * @param {*} start      - starting time of the range
 * @param {*} end        - engine time of the range
 * @param {*} duration   - unit of duration i.e days, months, etc.
 * @param {*} arr        - accumulating array
 * @returns 
 */
const getCustomDateRange = (start, end, duration, arr = [DURATION_MAP[duration](start)]) => {
    if(!isDate(start) || !isDate(end)) throw new Error('start/end must be a date');
    
    if(isAfter(start, end)) throw new Error('start must precede end')

    const next = DURATION_MAP[duration](add(start, { [duration]: 1 }));

    if(isAfter(next, end)) return arr;

    return getCustomDateRange(next, end, duration, arr.concat(next));
}

/**
 * Get an array of dates to use for transaction allocation
 * @returns an array with dates added for the time spanning the duration provided
 */
const getTransactionDates = () => {
    const START_DATE = getStartDate();
    const monthsArray = getCustomDateRange(START_DATE, addMonths(START_DATE, 5), 'months');
    const dateArray = [];
    monthsArray.forEach(month => {
        dateArray.push(getCustomDateRange(month, addDays(month, TRANSACTION_COUNT_PER_MONTH - 1), 'days'));
    });

    return dateArray;
}

/**
 * 
 * @typedef {Object} Config
 * @property {String} initTimestamp         - starting timestamp for the simulation
 * @property {Object[]} trancheConfigs      - Array of tranche configs objects
 * @property {Number} lowerBoundRiskScore   - lower limit of a tranche risk score
 * @property {Number} upperBoundRiskScore   - upper limit of a tranche risk score
 * @property {Number} initialValue          - dollar value of the tranche at initialization
 */

/**
 * Resolve configuration object for initialization of tranche engine
 * @param {String} startDate 
 * @returns {Config}
 */
const resolveConfigData = startDate => {
    return {
        initTimestamp: startDate,
        trancheConfigs: [
            {
                lowerBoundRiskScore: 0,
                upperBoundRiskScore: 25,
                initialValue: 1000000
            }, 
            {
                lowerBoundRiskScore: 25,
                upperBoundRiskScore: 50,
                initialValue: 2000000
            },
            {
                lowerBoundRiskScore: 50,
                upperBoundRiskScore: 100,
                initialValue: 5000000
            }
        ]
    }
};

/**
 * Derive risk score based on the input file record
 * @param {Object} obj - A row from the input file
 * @returns {Number} risk score
 */
const resolveConsumerRisk = (obj) => {
    const totalLoan = getBillArray(obj).reduce(addReducer, 0);
    
    const totalPayment = getPaymentsArray(obj).reduce(addReducer, 0);

    return totalLoan ? 0 : Math.round((totalPayment / totalLoan) * 100) || 0;
};

/**
 * 
 * @typedef {Object} Consumer
 * @property {Number} id                   - starting timestamp for the simulation
 * @property {Number} limitBalance         - lower limit of a tranche risk score
 * @property {String} sex                  - upper limit of a tranche risk score
 * @property {String} education            - dollar value of the tranche at initialization
 * @property {String} maritalStatus        - dollar value of the tranche at initialization
 * @property {Number} age                  - dollar value of the tranche at initialization
 * @property {Number} initialCreditScore   - dollar value of the tranche at initialization
 */

/**
 * Resolve the Consumer object based on the input file record
 * @param {Object} obj - A row from the input file
 * @param {Number} risk - derive risk score
 * @returns {Consumer}
 */
const resolveConsumer = (obj, risk) => {
    return {
        id: obj.ID,
        limitBalance: obj.LIMIT_BAL,
        sex: obj.SEX,
        education: obj.EDUCATION,
        martialStatus: obj.MARRIAGE,
        age: obj.AGE,
        initialCreditScore: risk || 0
    }
};

/**
 * 
 * @typedef {Object} Loan
 * @property {Number} id           - starting timestamp for the simulation
 * @property {String} timestamp    - lower limit of a tranche risk score
 * @property {Number} consumerId   - upper limit of a tranche risk score
 * @property {Number} amount       - dollar value of the tranche at initialization
 * @property {Number} merchantId   - dollar value of the tranche at initialization
 */

/**
 * Resolves the Loan object based on the input file record
 * @param {Object} obj 
 * @param {Array<String>} dateArray
 * @returns {Loan}
 */
const resolveConsumerLoan = (obj, dateArray) => {
    const billArray = getBillArray(obj);
    const loans = [];
    const len = merchants.length;

    billArray.forEach((bill, index) => {
        dateArray[index].forEach(timestamp => {
            if(bill !== 0) {
                const loanAmount = Math.round((bill/TRANSACTION_COUNT_PER_MONTH + Number.EPSILON) * 100) / 100;
                loans.push({
                    timestamp,
                    id: faker.datatype.number(ID_MIN, ID_MAX),
                    consumerId: getConsumerId(obj),
                    amount: loanAmount,
                    merchantId: merchants[Math.floor(Math.random() * len)].id
                });
            }
        })
    });

    return loans;
};

const resolveConsumerPayment = (obj) => {
    const paymentArray = getPaymentsArray(obj);
    return {
        id: faker.datatype.number(ID_MIN, ID_MAX),
        timestamp: '',
        consumerId: getConsumerId(obj),
        amount: ''
    }
}

module.exports = { 
    addReducer,
    getStartDate,
    resolveConsumer,
    resolveConfigData,
    resolveConsumerLoan,
    resolveConsumerRisk,
    getTransactionDates,
    resolveLoanOutputFileName,
    resolveConsumerOutputFileName    
}
