'use strict';
const { Constants, Logger } = require('./utils/');

const logger = new Logger(Constants.LOG_FILE, false);

/**
 * exceptionHandler
 * @param  {Object} error - handles exceptions and exit the application
 */
const exceptionHandler = (error) => {
    // NOTE: add any other error handling required here
    console.log({ error });
    process.exit(1);
};

/**
 * unhandledRejectionHandler
 * @param {*} promise 
 * @param {*} reason 
 */
const unhandledRejectionHandler = ({ message, stack }) => {
    console.log({
        isUnhandledRejection: true,
        message,
        stack
    });
};

/**
 * processWarningHandler
 * @param {Object} warning - the node warning object
 */
const processWarningHandler = ({ name, message, stack }) => {
    console.log({
        isProcessWarning: true,
        name,
        message,
        stack
    });
};

/**
 * bindAllHandlers
 * @param {*} process 
 */
const bindAllHandlers = (process) => {
    process.on('uncaughtException', exceptionHandler());
    process.on('unhandledRejection', unhandledRejectionHandler());
    process.on('warning', processWarningHandler());
};

module.exports = {
    exceptionHandler,
    unhandledRejectionHandler,
    processWarningHandler,
    bindAllHandlers
};
