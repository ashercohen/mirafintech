"use strict";

const path = require("path");

const logFolder= path.resolve("./logs/");
const generatorLogFile = logFolder + "generator.log";

const generatorContentFolder = path.resolve("./data/generator/");
const generatorContentFile = "/contents.json";
const generatorContentFilePath = generatorContentFolder + generatorContentFile;

module.exports = {
    JSON_FILE_FORMAT_CODE: 4,
    LOG_FOLDER: logFolder,
    GENERATOR_LOG_FILE: generatorLogFile,
    GENERATOR_CONTENT_FILE: generatorContentFile,
    GENERATOR_CONTENT_FOLDER: generatorContentFolder,
    GENERATOR_CONTENT_FILE_PATH: generatorContentFilePath,
    HTTP_METHODS: {
        GET: "get",
        POST: "post",
        PUT: "put",
        PATCH: "patch",
        DELETE: "delete"
    }
};
