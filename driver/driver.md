# Driver

## Running the simulation driver
### $ Setting up the project
    
    $ brew install node@12
    $ brew install yarn
    $ yarn
    
### $ Input
Input to the simulation driver will be the `UCI_Credit_card.csv` file.
### $ Simulation process
Navigate to the driver-node folder in the workspace

    $ cd {pwd}/driver/driver-node
    $ node index.js -f <filepath>
Following steps happen during the simulation process:
1. Firstly, the data from the source CSV file is streamed and transformed
   by the driver, into separate json files.
2. The configuration for the Trance Engine is initialized.
3. Then, each of the Merchants, Consumers, Loans and Payments records
   are ingested into the Tranche Engine.

Note: To stop the process mid-way, press cmd+C.
### $ Output
1. JSON files for Consumers, Loans and Payments.
2. All entities being synced to Tranche Engine.
## Purpose - drive the simulation
1. go through the dataset and call the various engine's endpoint .
2. control the time: engine's "virtual" time via time update messages.
3. control the simulation length/pace - how fast we "fast forward" time in order to squeeze 2-3 years of simulation into 3-15 minutes.

## Synchronous Mode of Operation
in order to simplify the engine's implementation, the engine assumes that the next call/message will be delivered only after the current message has been processed and successful return code (http 2xx) was sent and read by the driver.  

## Unit of Time - Day
1. For simplicity, the engine will make a periodic operations on a (virtual) daily basis. Such operations include (not an exhaustive list): check for late payments, apply interest, generate charges, etc. 
2. Set time messages (see below) supports DateTime, i.e. time within the day (including hours, minutes, seconds and millis) is supported/recorded.

## Messages

### set configuration
**Description**
1. pass the server initial configuration incl risk level setup (number of risk levels, range for each)
2. it is expected that first message of the simulation would be a configuration message via this endpoint
3. [exact details here](https://github.com/ashercohen/mirafintech/blob/main/engine/src/main/java/com/mirafintech/prototype/controller/MessageController.java)

### set time
**Description**
1. update/set the date and time of the system
2. format: ISO-8601. examples: 2021-05-08T17:15:30  (millis, microsecond are expressed as decimal fraction of a second but I doubt it will be required in the scope of this prototype).
3. "jumping" multiple days is allowed, the engine will compare the current (virtual) time to the new time and internally generate day updates until it reaches the new time as specified by the message
4. More info: https://docs.oracle.com/javase/8/docs/api/java/time/LocalDateTime.html

**Endpoint Details**   
1. POST, date time string is passed in the request body (plain text - not json)
2. [exact details here](https://github.com/ashercohen/mirafintech/blob/main/engine/src/main/java/com/mirafintech/prototype/controller/MessageController.java) 
### add consumer
**Description**
1. add a new consumer to the system
2. it is assumed that a consumer is added via this endpoint before any loan/transaction/... that references this consumer is sent to the engine.
3. consumer id - assumed that it is determined by the driver (probably already contained in the dataset)

**Endpoint Details**
1. POST, a json representation of the Consumer object is passed in the request body
2. [exact details here](https://github.com/ashercohen/mirafintech/blob/main/engine/src/main/java/com/mirafintech/prototype/controller/MessageController.java)
3. id of the consumer is to be passed as a path variable - this value must match the value specified in the json/request body

**TBD:** format/fields of a consumer entity

### add loan
**Description**
1. add a new loan **with timestamp**
2. system's virtual time will be updated accordingly
3. example:
`{"timestamp": "2021-05-08T17:15:30", "id": 1000, "consumerId": 12345, "amount": 100.0, "merchantId": 999}`
