#!/bin/sh

set -x

# Run chef-solo for configuration
cd chef-solo
chef-solo -c solo.rb -E ${ENVNAME} --log_level "debug" --logfile "/tmp/Chef-Solo.log"

status=$?
if [ $status != 0 ]; then
  echo "[ERROR] Problem detected while running chef. Aborting !"
  exit 1
fi

# Set command parameters 
CONF_FILE="/conf/conf.yaml"
CREDENTIALS_FILE="/conf/credentials.yaml"
LOGS_PROP_FILE="file:/conf/log4j.properties"
SUITE_FILE="/testSuite/testSuite.xml"
JAR_FILE="dcae-ci-tests-jar-with-dependencies.jar"
MainClass="org.onap.dcae.ci.run.RunTestSuite"

# Execute dcae-ci-tests
cd /

java -Dconfig.resource=${CONF_FILE} \
     -Dcredentials.file=${CREDENTIALS_FILE} \
     -Dlog4j.configuration=${LOGS_PROP_FILE} \
     -DtestSuite=${SUITE_FILE} \
     -cp $JAR_FILE ${MainClass} \
     > /logs/dcae_testSuite.out
