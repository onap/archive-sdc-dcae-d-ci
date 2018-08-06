FROM cypress/browsers:chrome65-ff57

USER root

ENV PATH /home/node/app/node_modules/.bin:$PATH

ENV npm_config_loglevel=warn

WORKDIR /home/node/app

COPY src/package.json .

RUN npm i npm@latest -g

RUN npm config set proxy http://one.proxy.att.com:8080
RUN npm config set https-proxy http://one.proxy.att.com:8080

RUN npm install --verbose --save \
                mocha@5.0.0 \
                mochawesome@3.0.2 \
                cypress@2.1.0 \
                cypress-testing-library@2.2.0

RUN /home/node/app/node_modules/.bin/cypress verify && \
    /home/node/app/node_modules/.bin/cypress --version

CMD ["cypress", "run", "--browser", "chrome"]
