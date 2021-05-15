const axios = require("axios");
const als = require("async-local-storage");

module.exports = async (url, data,method = "post") => axios.create({
    headers: {
        get "x-flow-id"() {
            return als.get("flow_id");
        },
    },
    url,
    method,
    data,
});

