# Driver

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
### set time
**Description**
1. update/set the date and time of the system
2. format: ISO-8601. examples: 2021-05-08T17:15:30  (millis, microsecond are expressed as decimal fraction of a second but I doubt it will be required in the scope of this prototype).
3. "jumping" multiple days is allowed, the engine will compare the current (virtual) time to the new time and internally generate day updates until it reaches the new time as specified by the message
4. More info: https://docs.oracle.com/javase/8/docs/api/java/time/LocalDateTime.html

**Endpoint Details**   
1. POST, date time string is passed in the request body (plain text - not json)
2. ~/messages/time/set
### add consumer
** Description **
1. add a new consumer to the system
2. it is assumed that a consumer is added via this endpoint before any loan/transaction/... that references this consumer is sent to the engine.
3. consumer id - assumed that it is determined by the driver (probably already contained in the dataset)

**Endpoint Details**
1. POST, a json representation of the Consumer object is passed in the request body
2.  ~/consumer/{id}
3. id of the consumer is to be passed as a path variable - this value must match the value specified in the json/request body

**TBD:** format/fields of a consumer entity