"use strict";

const path = require("path");
const fs = require("fs");

/**
 * Create a folder if it does not exist.
 * @param {string} pathToCreate - folder path.
 */
const createDirectory = (pathToCreate) => {
    pathToCreate.split(path.sep)
        .reduce((currentPath, folder) => {
            const nextFolder = currentPath + folder + path.sep;
            if (!fs.existsSync(nextFolder)) {
                try {
                    fs.mkdirSync(nextFolder);
                } catch (err) {
                    if (err.code !== "EEXIST") {
                        throw err;
                    }
                }
            }
            return nextFolder;
        }, "");
};

/**
 * Write content into file sync-ly.
 * @param {string} filePath - full file name
 * @param {string} data     - content text
 */
const createNewFile = (filePath, data = "") => {
    if (fs.existsSync(filePath)) {
        fs.unlinkSync(filePath);
    }
    fs.writeFileSync(filePath, data);
};

/**
 * Check if a file exist sync-ly.
 * @param {string} filePath - full file name
 * @return {boolean}
 */
const fileExist = filePath => {
    return fs.existsSync(filePath);
};

const writeDataToFile = (filePath, data) => {
    if(!data) return;

    fs.appendFileSync(filePath, data);
}

/**
 * Get file age in ms.
 * @param {string} filePath - full file name.
 * @return {number}
 */
const getFileAge = filePath => {
    const cTime = new Date().getTime();
    const stats = fs.statSync(filePath);

    return cTime - stats.ctimeMs;
};

module.exports = {
    createDirectory,
    fileExist,
    createNewFile,
    getFileAge,
    writeDataToFile
};
