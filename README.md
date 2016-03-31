[![Build Status](https://travis-ci.org/RxHandsOn/MarketData.svg?branch=master)](https://travis-ci.org/RxHandsOn/MarketData)

# MarketData
## Java
TODO

## TypeScript
A simple project to help you get started with RxJS and Typescript. It contains an html page where systemJS is configured with the Typescript transpiler, everything is ready to start coding in file index.ts.

### Build
First step is to retrieve the dependencies:

    npm install

Then you can run the tests with the following command:

    npm test

### How to run the application
You need to launch 2 java processes:

 1. One java process running class **Market**. This is our fake market data providers
 2. One Java process running class **Application**. This class will run several HTTP netty servers that will be used by the Typescript UI. To open the UI in your browser just use URL [http://localhost:8000](http://localhost:8000)
