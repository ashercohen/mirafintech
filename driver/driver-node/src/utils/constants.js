"use strict";

const path = require("path");

const logFolder = path.resolve("./logs/");
const logFile = logFolder + "/info.log";

module.exports = {
    JSON_FILE_FORMAT_CODE: 4,
    LOG_FOLDER: logFolder,
    LOG_FILE: logFile,
    HTTP_METHODS: {
        GET: "get",
        POST: "post",
        PUT: "put",
        PATCH: "patch",
        DELETE: "delete"
    }
};
