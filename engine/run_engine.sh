#!/bin/sh

set -o nounset
set -o errexit

java --enable-preview -jar target/engine-1.0.jar

