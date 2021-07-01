# mirafintech - Mira Prototype

default branch: *main*  (the "master" has retired)

## Datasets

## Environment Setup

### java openjdk 16 - Linux/Mac/Windows
info: https://openjdk.java.net/projects/jdk/16/ \
binaries: https://jdk.java.net/16/ \
Oracle JDK/Java SDK (not JRE) probably works as well - I didn't test it

### maven 3.8.1 (or higher) - on Mac, do NOT use brew
**maven added to the repository - no need to install it** \
info: https://maven.apache.org/download.cgi

### IDE IntellijIDEA 2021.1 - Required for coding, not required for running
https://www.jetbrains.com/idea/download/ \
if using another IDE make sure Java 16 is supported

### Setting Up PostgresSQL Locally - Linux/Ubuntu (MacOS should be very similar - path prefixes probably differ)
#### install postgres 12
#### prepare data dir
`/usr/lib/postgresql/12/bin/pg_ctl init -D ~/postgresql/12/main/`
#### start server
`/usr/lib/postgresql/12/bin/pg_ctl -D ~/postgresql/12/main -l ~/postgresql/12/logs/log.txt start`
#### create user/role
`/usr/lib/postgresql/12/bin/createuser -d -P mirafintech` \
when prompt for password enter `mirafintech` and reconfirm 
#### create database
`/usr/lib/postgresql/12/bin/createdb mirafintech`
#### command line client - psql
`psql -d mirafintech` \
(type `exit` to quit) \
if it complains about missing user, create additional user/role as your machine's login user

### Setting Up PostgresSQL Locally - Windows 10

download postgres installer from https://www.enterprisedb.com/downloads/postgres-postgresql-downloads \
   make sure to select version **12.7 Windows x86-64**

### installation
1. run installer
2. accept default value for installation directory (`C:\Program Files\PostgreSQL\12`)
3. accept defaults for components (all 4 selected)
4. Data Directory  **very important**: 
   - create a directory named `data` under you home directory's Documents: `C:\Users\Asher\Documents\data`
   - point the installer to this directory
5. Password: enter `mirafintech` (twice) - this is the admin password (admin username is `postgres`)
6. Port accept default (5432)
7. Locale accept default
8. run installer to completion - if "stack builder" is starting, close it

### Setting up user/role/database - command line practice
1. open `cmd` (Command Line) and cd to postgres bin directory: `C:\Program Files\PostgreSQL\12\bin`
2. define environment variable named `PGDATA` (this would temporarily to this cmd session) to point to the data directory created on installation step 4: \
   `set PGDATA=C:\Users\Asher\Documents\data`
3. verify env var created successfully using:
   `set` 
   scroll up the list and verify that `PGDATA` is defined with the correct value
4. create `logs` directory next to the `data` directory: `C:\Users\Asher\Documents\logs`
5. start the database server (make sure you're at postgres bin directory) \
   `pg_ctl -D "C:\Users\Asher\Documents\data" -l "C:\Users\Asher\Documents\logs\log.txt" start`
6. create user/role \
   `createuser.exe -U postgres -W -d -P mirafintech` - when prompt for password enter `mirafintech` 3 times (crazy windows port...)
7. create database \
   `createdb -U postgres -W mirafintech` - if prompt for password use... `mirafintech`
8. check that everything created successfully: \
   `psql -U postgres -d mirafintech` (password=`mirafintech`) \
   in the psql tool run: \
   `\l` to list databases: `mirafintech` should appear in the list \
   `\du` to list users/roles: `mirafintech` should appear in the list \
   `\q` to exit the tool

### Building and Running the engine
- scripts are for bash/cmd and located under `engine` directory \
- open bash/cmd and cd to `engine` directory and run:

#### Linux/Mac
- build: [build_engine.sh](https://github.com/ashercohen/mirafintech/blob/main/engine/build_engine.sh)
- run: [run_engine.sh](https://github.com/ashercohen/mirafintech/blob/main/engine/run_engine.sh)

#### Windows
- build: [build_engine_windows.bat](https://github.com/ashercohen/mirafintech/blob/main/engine/build_engine_windows.bat)
- run: [run_engine_windows.bat](https://github.com/ashercohen/mirafintech/blob/main/engine/run_engine_windows.bat)
