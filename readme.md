# Communication Between Android and PC via USB

## 1. Technology Choices

### 1.1 Using Sockets for Communication

> Why Sockets can be used for communication over USB:
> USB cable provides a physical layer data transmission channel between the PC and Android phone. ADB can utilize this channel to create network ports on both ends and forward data. 
> Specifically, the `adb forward` command maps port 8000 on the PC side and port 9000 on the phone side. Data received on port 8000 of the PC will be forwarded to the phone side over USB.  
> From the network protocol perspective, port 8000 on the PC side now provides a TCP connection to port 9000 on the phone side.
> Therefore, the Socket interface at the application layer can send and receive data over this TCP connection, without worrying about whether the underlying layer is USB, WiFi or other networks.
> The Input/Output streams read and write data forwarded between different ports via the Socket.
> In summary, adb utilizes the USB cable to establish TCP port forwarding and data forwarding on both ends. The application layer code leverages the Socket interface based on TCP protocol to communicate with the peer.

### 1.2 Reason for Not Using `android.hardware.usb`

- The library is mainly used for communication between Android and USB devices
- Tried several open source libraries, unable to directly read USB port data on the PC side

#### 2. Technologies Used

### 2.1 PC Side

- Java language
- Socket class: Java side only acts as Socket client
- adb command (adb path needs to be configured in environment variable beforehand) 
- adb forward: establish port forwarding over USB between ports 8000 and 9000

### 2.2 Android Side

- Java language
- Socket class: acts as both server to establish connection, and client to send/receive data

## 3. Running and Usage

### 3.1 Run Android side first

1. Can edit text in the `INPUT EDIT` section on top, press Enter to send edited text to PC side
2. `RECEIVE TEXT` section at bottom will automatically display text received from PC side

### 3.2 Then run PC side

Similar to Android side, but text is sent/received via command line
