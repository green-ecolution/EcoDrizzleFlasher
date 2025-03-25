Welcome to the EcoDrizzleFlasher wiki!

![image](https://github.com/user-attachments/assets/c69e28cd-44ec-44e7-8d64-447bad7f4fd9)

## Introduction

Smart irrigation is needed to save water, staff and costs. This wiki is the documentation for the user-interface for Green Ecolution. The user-interface allows users to connect to the backend and interact with it's database. Interactions are possible with:

- trees
- tree clusters
- sensors
- watering plans
- vehicles
- users

While the project is created in collaboration with the local green space management (TBZ Flensburg), this software aims to be applicable to other cities. It was initially developed at the University of Applied Sciences Flensburg as a research project within the Applied Computer Science masters degree program.

## Content of this Repository
This wiki documents a flasher tool that can be used, for example, to equip a Heltec ESP32 microcontroller with the necessary software to send data to the TTN. This consists of two main functions. Firstly, the sensor must be created in the TTN. Then the microcontroller can be flashed with the necessary software. After this process, the microcontroller can send sensor data directly to the TTN without any further settings, which can be viewed and used by the user.

<p align="center">
  <img src="https://github.com/user-attachments/assets/00309ad8-30b6-431d-8dd4-2fe04b909e1c" alt="Bildbeschreibung" width="60%">
</p>

To register the microcontroller in the TTN the HTTP(REST)-API is used: https://www.thethingsindustries.com/docs/api/reference/http/

The Arduino CLI is used to flash the microcontroller: https://arduino.github.io/arduino-cli/1.2/

## Further information
For more information about this project please refer to:
- [Project website](https://green-ecolution.de/)
- [University of Applied Sciences Flensburg](https://hs-flensburg.de/)
