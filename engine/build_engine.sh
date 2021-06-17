#!/bin/sh

set -o nounset
set -o errexit

../tools/apache-maven-3.8.1/bin/mvn clean install -f pom.xml
