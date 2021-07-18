'use strict';

const path = require('path');
const faker = require('faker');
const { 
    add, isDate, isAfter,
    addDays, startOfDay,
    startOfHour, startOfYear, startOfToday,
    startOfMonth, startOfMinute, startOfSecond,
    lastDayOfMonth, format
} = require('date-fns');

const merchants = require('../../data/source/Merchant.json');

const MIRA_INTEREST = 0.025;
const LOW_INTEREST  = 0.075;
const MED_INTEREST  = 0.095;
const MED_HIGH_INTEREST = 0.011;
const HIGH_INTEREST  = 0.125;
const DEFAULT_PAYMENT_POLICY = 'defaultPaymentPolicy';

const ID_MAX = 2000000000;
const ID_MIN = 1000000000;
const getID = () => faker.datatype.number(ID_MIN, ID_MAX);
const TRANSACTION_COUNT_PER_MONTH = 5;
const DURATION_MAP = {
    seconds: startOfSecond,
    minutes: startOfMinute,
    hours: startOfHour,
    days: startOfDay,
    months: startOfMonth,
    years: startOfYear
};

const addReducer = (accumulator, currentValue) => accumulator + currentValue;

const getBillArray = obj => [obj.BILL_AMT1, obj.BILL_AMT2, obj.BILL_AMT3, obj.BILL_AMT4, obj.BILL_AMT5, obj.BILL_AMT6];
const getPaymentsArray = obj => [obj.PAY_AMT1, obj.PAY_AMT2, obj.PAY_AMT3, obj.PAY_AMT4, obj.PAY_AMT5, obj.PAY_AMT6];
const getPayDelayArray = obj => [obj.PAY_0, obj.PAY_2, obj.PAY_3, obj.PAY_4, obj.PAY_5, obj.PAY_6];
const getConsumerId = obj => obj.ID;
const getLimitBal = obj => obj.LIMIT_BAL;

const getISOStartDate = () => startOfYear(startOfToday());
const getServerStartDate = () => format(startOfYear(startOfToday()), 'yyyy-MM-dd\'T\'HH:mm:ss');
const getRandomNumber = (min, max) => Math.random() * (max - min) + min;
const resolveFileName = file => path.basename(file, path.extname(path.resolve(file)));
const resolveConsumerOutputFileName = file => resolveFileName(file) +'_consumers.json';
const resolveTransactionOutputFileName = file => resolveFileName(file) +'_transactions.json';

/**
 * Get a custom date range array based on below params
 * @param {ISODate} start      - starting time of the range
 * @param {ISODate} end        - end time of the range
 * @param {String} duration   - unit of duration i.e days, months, etc.
 * @param {Array} arr        - accumulating array
 * @returns 
 */
const getCustomDateRange = (start, end, duration, arr = [DURATION_MAP[duration](start)]) => {
    if(!isDate(start) || !isDate(end)) throw new Error('start/end must be a date');
    
    if(isAfter(start, end)) throw new Error('start must precede end');

    const next = DURATION_MAP[duration](add(start, {
        [duration]: 1, 
        hours: getRandomNumber(0, 24), 
        minutes: getRandomNumber(0, 60),
        seconds: getRandomNumber(0, 60)
    }));

    if(isAfter(next, end)) return arr;

    return getCustomDateRange(next, end, duration, arr.concat(next));
};

const getRandomDate = (start, end) => format(start.getTime() + Math.random() * (end.getTime() - start.getTime()), 'yyyy-MM-dd\'T\'HH:mm:ss');

const getRandomDatesArray = (start, end, numOfDays) => {
    const dateArray = [];
    while(numOfDays) {
        dateArray.push(getRandomDate(start, end));

        numOfDays--;
    }

    return dateArray;
};

const getRandomDatesInAMonth = (start, numOfDays) => {
    const end = lastDayOfMonth(start);
    
    return getRandomDatesArray(start, end, numOfDays);
};

/**
 * Get an array of dates to use for loan transaction allocation
 * @returns an array with dates added for the time spanning the duration provided
 */
const getRandomDatesAcrossMonths = (numOfMonths, start = getISOStartDate()) => {
    const dateArray = [];
    while(numOfMonths) {
        dateArray.push(getRandomDatesInAMonth(start, TRANSACTION_COUNT_PER_MONTH));
        start = addDays(lastDayOfMonth(start), 1);

        numOfMonths--;
    }

    return dateArray;
};

/**
 * Get an array of dates to use for payments transaction allocation
 * @returns an array with dates added for the time spanning the duration provided
 */
const getRandomPaymentDates = (numOfMonths, start = getISOStartDate()) => {
    const dateArray = [];
    while(numOfMonths) {
        const end = lastDayOfMonth(start);
        dateArray.push(getRandomDate(start, end));
        start = addDays(end, 1);

        numOfMonths--;
    }

    return dateArray;
};

/**
 * 
 * @typedef {Object} Config
 * @property {String} initTimestamp            - starting timestamp for the simulation
 * @property {String} paymentAllocationPolicy  - allocation policy
 * @property {Number} miraInterest             - interest rate for Mira
 * @property {Object[]} trancheConfigs         - Array of tranche configs objects
 * @property {Number} lowerBoundRiskScore      - lower limit of a tranche risk score
 * @property {Number} upperBoundRiskScore      - upper limit of a tranche risk score
 * @property {Number} initialValue             - dollar value of the tranche at initialization
 * @property {Number} interest                 - tranche interest value
 */

/**
 * Resolve configuration object for initialization of tranche engine
 * @param {String} startDate 
 * @returns {Config}
 */
const resolveConfigData = startDate => {
    return {
        initTimestamp: startDate,
        miraInterest: MIRA_INTEREST,
        paymentAllocationPolicy: DEFAULT_PAYMENT_POLICY,
        gracePeriodLength: 14,
        trancheConfigs: [
            {
                lowerBoundRiskScore: -9999,
                upperBoundRiskScore: -172,
                initialValue: 1000000,
                interest: LOW_INTEREST
            }, 
            {
                lowerBoundRiskScore: -171,
                upperBoundRiskScore: 661,
                initialValue: 1000000,
                interest: MED_INTEREST
            },
            {
                lowerBoundRiskScore: 662,
                upperBoundRiskScore: 1278,
                initialValue: 1000000,
                interest: MED_HIGH_INTEREST
            },
            {
                lowerBoundRiskScore: 1279,
                upperBoundRiskScore: 99999,
                initialValue: 1000000,
                interest: HIGH_INTEREST
            }
        ]
    };
};

/**
 * Derive risk score based on the input file record
 * @param {Object} obj - A row from the input file
 * @returns {Number} risk score
 */
const resolveConsumerRisk2 = (obj) => {
    const totalLoan = getBillArray(obj).reduce(addReducer, 0);

    if (totalLoan === 0 )
        return 0;

    const totalPayment = getPaymentsArray(obj).reduce(addReducer, 0);

    return totalLoan ? 0 : Math.round((totalPayment / totalLoan) * 100) || 0;
};

/**
 * Derive risk score based on the input file record
 * @param {Object} obj - A row from the input file
 * @returns {Number} risk score
 * avg pay delay
    max of the pay delay array
    diff between avg and max value
    max bill amount, limit balance
    (limit balance) / (max bill)
    risk = 2 * Avg Pay + (Max Pay)/2 + Utilization
 */
const resolveConsumerRisk = (obj) => {
    const paymentDelayArray = getPayDelayArray(obj);
    const avgPayDelay = (paymentDelayArray.reduce(addReducer, 0) / paymentDelayArray.length).toFixed(2);
    const maxPayDelay = Math.max(...paymentDelayArray);
    const diffPay = maxPayDelay - avgPayDelay;
    
    const maxBill = Math.max(...getBillArray(obj));
    const limitBalance = getLimitBal(obj);
    const utilization = maxBill / limitBalance;

    const risk = parseInt(((2 * avgPayDelay + utilization + diffPay / 2) * 1000).toFixed(0), 0) || 0;

    return risk;
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
    };
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
const resolveConsumerLoan = (obj) => {
    const dateArray = getRandomDatesAcrossMonths(6);
    const billArray = getBillArray(obj);
    const loans = [];
    const len = merchants.length;

    billArray.forEach((bill, index) => {
        dateArray[index].forEach(timestamp => {
            if(bill !== 0) {
                const loanAmount = Math.round((bill/TRANSACTION_COUNT_PER_MONTH + Number.EPSILON) * 100) / 100;
                loans.push({
                    type: 'loans',
                    timestamp,
                    id: getID(),
                    consumerId: getConsumerId(obj),
                    amount: loanAmount,
                    merchantId: merchants[Math.floor(Math.random() * len)].id
                });
            }
        });
    });

    return loans;
};

const resolveConsumerPayment = (obj) => {
    const consumerId = getConsumerId(obj);
    const payments = getPaymentsArray(obj);
    const dateArray = getRandomPaymentDates(6);
    const paymentObj = [];

    for(let i = 0; i < payments.length; i++) {
        paymentObj.push({
            type: 'payments',
            id: getID(),
            timestamp: dateArray[i],
            consumerId,
            amount: payments[i]
        });
    }

    return paymentObj;
};

module.exports = { 
    addReducer,
    resolveConsumer,
    resolveConfigData,
    resolveConsumerLoan,
    resolveConsumerRisk,
    resolveConsumerRisk2,
    resolveConsumerPayment,
    getServerStartDate,
    getRandomPaymentDates,
    getRandomDatesAcrossMonths,
    resolveConsumerOutputFileName, 
    resolveTransactionOutputFileName
};
