# mirafintech - Mira Prototype

default branch: *main*  (the "master" has retired)

## Datasets

## Environment Setup

### java openjdk 16  
info: https://openjdk.java.net/projects/jdk/16/ \
binaries: https://jdk.java.net/16/

### maven 3.8.1 (or higher) - on Mac, do NOT use brew
info: https://maven.apache.org/download.cgi

### IDE IntellijIDEA 2021.1
https://www.jetbrains.com/idea/download/ \
if using another IDE make sure Java 16 is supported

### Setting Up PostgresSQL Locally
(tested on Linux/Ubuntu)
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
(type exit to quit) \
if it complains about missing user, create additional user/role as your machine's login user
