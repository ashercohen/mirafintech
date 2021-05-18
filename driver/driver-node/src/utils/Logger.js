"use strict";

const logger = require("log4js");
let instance = null;

class Logger {
    /**
     * Singleton constructor for logger
     * @param {Object} options configuration for the log4js logger
     */
    constructor(filename, forceNew = false) {
        const options = {
            appenders: { "app": { type: "file", filename, layout: { type: "basic" } } },
            categories: { default: { appenders: ["app"], level: "all" } }
        };

        if (instance) {
            if (forceNew) {
                instance = this.createLogger(options);
            }
            return instance;
        }
        instance = this.createLogger(options);

        return instance;
    }

    /**
     * Create a log4js logger
     */
    createLogger(options) {
        if (!options) {
            throw new Error("Missing a logger config.");
        }

        logger.configure(options);
        return logger.getLogger();
    }

}

module.exports = Logger;
