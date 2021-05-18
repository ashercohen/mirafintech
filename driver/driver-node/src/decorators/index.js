"use strict";

const fs = require("fs");
const path = require("path");
const baseName = path.basename(__filename);
const EXTENSION_LENGTH = -3; //length of the file extension to filter

const decorators = {};

fs.readdirSync(__dirname)
    .filter((file) => file.indexOf(".") !== 0 && (file !== baseName) && (file.slice(EXTENSION_LENGTH) === ".js"))
    .forEach((file) => {
        const ctor = require(path.join(__dirname, file)); 
        if (typeof ctor !== "function") throw TypeError(`${file} should export a decorator function.`);
        decorators[file.slice(0,EXTENSION_LENGTH)] = ctor;
    });

module.exports = decorators;