# EcoDrizzleFlasher
A tool to flash esp32 with th option to log into ttl


Using arduino-cli (https://arduino.github.io/arduino-cli/1.1/installation/#download)
command to list all boards: ./arduino-cli.exe board list
command to upload a sketch: ./arduino-cli.exe upload -p COM3 --fqbn esp32:esp32:heltec_wifi_lora_32_V3 <path_to_sketch>

---

### Connecting Sensor with TTN
#### All the information required to connect a sensor to the TTN:
1. End-Device-Brand: HelTec AutoMation
2. Model: Wifi Lora 32 (V2) (Class A OTAA)
3. Hardwareversion: Unknown ver.
4. Firmwareversion: 1.0
5. Profile (Region): EU_863_870
6. Frequency Plan: Europe 863-870 MHz (SF12 for RX2)

---

##### TTN Credentials that need to be flashed on the Microcontroller to connect to the TTN:
1. End-Device-ID: ***ID structure needs to be clarified!***
2. JoinEUI (AppEUI): 00 00 00 00 00 00 00 00
3. DevEUI: A0 B1 C2 D3 E4 F5 G6 H7
4. AppKey: A0 B1 C2 D3 E4 F5 G6 H7 I8 J9 K0 L1 M2 N3 O4 P5

The Credentials should be formatted like the following examples for the code on the microcontroller. Normally, these 
values are generated directly in the TTN interface by the User.

***MSB (Most Significant Byte) is used to store the credentials***
1. JoinEUI (AppEUI): *0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00*
2. DevEUI: *0xA0, 0xB1, 0xC2, 0xD3, 0xE4, 0xF5, 0xG6, 0xH7*
3. AppKey: *0xA0, 0xB1, 0xC2, 0xD3, 0xE4, 0xF5, 0xG6, 0xH7, 0xI8, 0xJ9, 0xK0, 0xL1, 0xM2, 0xN3, 0xO4, 0xP5*

```
// Example for Arduino-Code
uint8_t appEui[] = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
uint8_t devEui[] = { 0xA0, 0xB1, 0xC2, 0xD3, 0xE4, 0xF5, 0xG6, 0xH7 };
uint8_t appKey[] = { 0xA0, 0xB1, 0xC2, 0xD3, 0xE4, 0xF5, 0xG6, 0xH7, 0xI8, 0xJ9, 0xK0, 0xL1, 0xM2, 0xN3, 0xO4, 0xP5 };
```

Other Parameters (Device adress, NwkSKey, AppSKey) should be generated automatically by the TTN.

---
