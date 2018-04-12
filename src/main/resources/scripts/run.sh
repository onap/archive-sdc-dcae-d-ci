#!/bin/bash

function help_usage ()
{
	echo
	echo "$0 (<jar_file_name> <suite file name>)"
	echo
	exit 2
}

function isBoolean ()
{
	PARAM_NAME=$1
	VALUE=$2
	if [[ ${VALUE} != "true" ]] && [[ ${VALUE} != "false" ]]; then
		echo "Valid parameter" ${PARAM_NAME} "values are: true/false"
	        help_usage
	fi	
}

#main
[ $# -lt 2 ] && help_usage

JAR_FILE=$1
SUITE_FILE=$2

CURRENT_DIR=`pwd`
BASEDIR=$(dirname $0)
if [ ${BASEDIR:0:1} = "/" ]
then
        FULL_PATH=$BASEDIR
else
        FULL_PATH=$CURRENT_DIR/$BASEDIR
fi
LOGS_PROP_FILE=file:${FULL_PATH}/conf/log4j.properties
CONF_FILE=${FULL_PATH}/conf/conf.yaml
CREDENTIALS_FILE=${FULL_PATH}/conf/credentials.yaml
MainClass=com.att.ecomp.dcae.ci.run.RunTestSuite


cmd="java -Dconfig.resource=${CONF_FILE} -Dcredentials.file=${CREDENTIALS_FILE} -Dlog4j.configuration=${LOGS_PROP_FILE} -DtestSuite=${SUITE_FILE} -cp $JAR_FILE ${MainClass} &"
$cmd

status=`echo $?`

echo "##################################################"
echo "################# status is ${status} #################" 
echo "##################################################"

exit $status
