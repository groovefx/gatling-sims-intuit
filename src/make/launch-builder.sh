#!/bin/sh

BUILD_TO=build/gatling-dse-sims

cat src/make/gatling-dse-launcher.sh build/libs/gatling-dse-sims-*.jar > ${BUILD_TO} && chmod 755 ${BUILD_TO}