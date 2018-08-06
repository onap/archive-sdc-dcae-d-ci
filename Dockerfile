FROM onap/base_sdc-sanity:1.3.1

RUN apk add --no-cache \
        shadow \
        && :

RUN mkdir ExtentReport logs

COPY chef-solo chef-solo
COPY target/dcae-ci-tests-*-jar-with-dependencies.jar dcae-ci-tests-jar-with-dependencies.jar
COPY target/classes/testSuite/testSuite.xml /testSuite/testSuite.xml
COPY target/classes/conf conf
COPY startup.sh startup.sh

RUN set -x ; \
    groupadd -g 35953 -f dcae ; \
    useradd -u 352070 -g dcae -Gdcae -m -d /home/dcae dcae && exit 0 ; exit 1

RUN chmod 775 startup.sh /testSuite/testSuite.xml /chef-solo /conf /ExtentReport /logs ; \
    chown dcae:dcae startup.sh /testSuite/testSuite.xml /chef-solo /conf /ExtentReport /logs

USER dcae

ENTRYPOINT ["./startup.sh"]