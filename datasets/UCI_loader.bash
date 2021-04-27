#!/bin/bash -e
input="UCI_Credit_Card.100.csv"
while IFS= read -r line
do
  echo "$line"

  curl --location --request POST 'http://localhost:8080/transactions/write/' \
  --header 'Content-Type: text/plain' \
  --data-raw "$line"

#break
done < <(tail -n +2 $input)