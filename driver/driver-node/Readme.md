# Simulation Driver with NodeJS

## Setting up the node runtime on Windows
  ### Installing Nodejs: vist [NodeJS website](https://nodejs.org/en/) and download the latest installation package. Install it.
  ### Once installed, NodeJS comes with Node Package Manager(NPM) out of the box, which can be used to manage node packages.
  ### Run the following command on command prompt: 
  `$ npm install --global yarn`
  ###  Download [mirafintech](https://github.com/ashercohen/mirafintech) project from GitHub and navigate to the driver-node folder using command prompt: 
  `$ cd mirafintech/driver/driver-node`
  ###  Install project dependencies by running following command on command prompt: 
  `$ yarn`
  ###  To start the simulation, run the following command by providing the source file path: 
  `$ node index.js -f <filepath>`


## Setting up the project on Mac
  ### Installation:
  `$ brew install node@12`
  `$ brew install yarn`
  `$ yarn`

  ### Run simulation:
  `$ cd {pwd}/driver/driver-node`
  `$ node index.js -f <filepath>`