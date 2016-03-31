[![Build Status](https://travis-ci.org/RxHandsOn/MarketData.svg?branch=master)](https://travis-ci.org/RxHandsOn/MarketData)

# MarketData
## Java
The project is based on Java8. As a prerequisite you also need maven3.
There is 3 modules:

1. marketdata-common: infrastructure code, mostly related to RxNetty. You do not need to change anything in this module
2. marketdata-external: fake marketdata providers. You just need to run class **Market**. No code change required during the handson
3. marketdata-web: the module where you will write code! Class **Application** launch a bunch of tiny HTTP servers, listening on different ports, that will be used by the Typescript UI (see below). Also these tiny servers relies on fake providers from marketdata-external.

## TypeScript
The whole UI is written in Typescript. You can either 'transpile' the code using the command line or directly within the browser. Everything is already set up.

### Build
First step is to retrieve the dependencies:

    npm install

Then you can run the tests with the following command:

    npm test

### How to run the application
You need to launch 2 java processes:

 1. One java process running class **Market**. This is our fake market data providers
 2. One Java process running class **Application**. This class will run several HTTP netty servers that will be used by the Typescript UI. To open the UI in your browser just use URL [http://localhost:8000](http://localhost:8000)
