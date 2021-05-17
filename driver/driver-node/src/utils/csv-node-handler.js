const Promise = require("bluebird");
const csv = require("csv");
const csvParse = Promise.promisify(csv.parse);
const csvStringify = Promise.promisify(csv.stringify);
const fs = Promise.promisifyAll(require("fs"));
const async = require("async");

class Csv {

    constructor({filePath, columns, autoCreate = false}) {

        this.filePath = filePath;
        this.write_started = false;
        this.columns = columns;
        // TODO: create assertions

        // initializations
        if (autoCreate) {

            this.createFileIfNotExist();

        }

        this.asyncQueue = async.queue((task, callback) => {
            console.log("Processing task in queue.");
            task()
                .then(() => {
                    if (callback) {
                        callback();
                    }
                });
        });

    }

    /**
     *  should create file if non-existent and
     */
    createFileIfNotExist() {

        if (!fs.existsSync(this.filePath)) {

            fs.openSync(this.filePath, "a");
            // debug("CSV -> file created!");

        }

    }

    /**
     *  write payload to file
     * @param payload (Javascript object -- fields should correspond to columns)
     */
    writeRow(payload) {

        let resultPromise;

        if (this.write_started) {

            resultPromise = csvStringify(payload, {});

        } else {

            this.write_started = true;
            resultPromise = csvStringify(payload, {columns: this.columns, header: true});

        }

        return resultPromise
            .then((stringified) => {
                return fs.appendFileAsync(this.filePath, stringified);
            })
            .then(() => {
            });

    }

    writeErrRow(payload) {

        return csvStringify(payload)
            .then((stringified) => {
                return fs.appendFileAsync(this.filePath, stringified);
            })
            .then(() => {
            });
    }

    read() {

        if (fs.existsSync(this.filePath)) {

            const read_buffer = fs.readFileSync(this.filePath);
            return csvParse(read_buffer, {columns: true});

        }

    }

    /** STATIC METHODS - CSV OPERATIONS * */
    static read(filePath) {

        if (fs.existsSync(filePath)) {

            const read_buffer = fs.readFileSync(filePath);
            return csvParse(read_buffer, {columns: true});

        }

    }

}

module.exports = Csv;

