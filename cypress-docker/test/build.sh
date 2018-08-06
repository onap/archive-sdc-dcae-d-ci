set e+x

LOCAL_NAME=dockercentral.it.att.com:5100/com.att.sdc/dcae-cypress:1.0.0

echo "[INFO] Building $LOCAL_NAME"

docker build -t $LOCAL_NAME \
       --build-arg HTTP_PROXY=http://one.proxy.att.com:8080 \
       --build-arg HTTPS_PROXY=http://one.proxy.att.com:8080 \
       .

echo "[INFO] Done"
