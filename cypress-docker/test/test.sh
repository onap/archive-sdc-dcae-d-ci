set e+x

LOCAL_NAME=dockercentral.it.att.com:5100/com.att.sdc/dcae-cypress:1.0.0

CONTAINER_NAME='cypress-test'

echo "Running tests against $LOCAL_NAME"

docker run -it --name ${CONTAINER_NAME} -v $PWD/src:/test -w /test $LOCAL_NAME

docker rm ${CONTAINER_NAME}


