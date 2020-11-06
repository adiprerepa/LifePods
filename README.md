# LifePods
LifePods is a safety tool. It is a dead man switch extention to your phone that helps you when you feel unsafe.

## Explanation
### The Problem
Take this scenario: You are walking through a shady area late at night, and you feel that you may be in danger, and you want to be safe if something happens to you. But you cannot take your phone out, or it is not to the extent that you have to leave the area.

### The Solution
If you find yourself in the scenario above, all you need to do is hold a lifepod, and continue what you were doing. If something happens to you, such as getting mugged, then you would release the lifepod discreetly. Once you release and do not deactivate within a 10-second window, everyone in your circle will be alerted that you might be in danger, with your gps coordinates. The people notified could then take appropriate action to enmsure your safety. There are 3 parts to this project: Bluetooth Button (A LifePod), Mobile App, and the Server. Together, they produce a system that helps you in times of danger.

![Architecture](https://github.com/adiprerepa/LifePods/blob/master/Blank%20Network%20Diagram.png)

## Raspberry Pi Setup
### Steps:
```
sudo apt-get install bluez python-bluez
sudo hciconfig hci0 piscan
sudo hciconfig hci0 name 'LifePodDevice'
<- Insert bluetoothCtl stuff here
sudo sdptool add SP
sudo systemctl daemon-reload
sudo service bluetooth restart
```

### BluetoothCtl Steps:
First, get into bluetoothctl mode, by running:
```
sudo bluetoothctl
```
Now, register the agent, make the device discoverable and pairable
``` 
$ [bluetoothctl] default-agent
$ [bluetoothctl] agent on
$ [bluetoothctl] discoverable on
$ [bluetoothctl] pairable on
```

After this, pair the raspberry pi with the android smartphone. Go to bluetooth devices, and there should be a list of available devices. Select "LifePodDevice". A prompt should appear on `bluetoothctl`, with a pairing code, and a prompt should also appear on the phone. Select "yes" on both. The devices are now paired.
see `rpisetup.sh` for more detailed instructions, in `LifePod/Raspberry/rpisetup.sh`.
- To Execute the program: `sudo python rpiServer.py`

## Server
The server uses gRPC, a lightweight, high-performant rpc framework. The supported RPCs(think HTTP GET & POST) are:
- updateThreatPriority: sends switch state when there is a change in state, and GPS coordinates of user.
- listEvents: sends back a list of events pertaining to a user.
- getFirebaseNotificationTopics - sends firebase topics that the client needs to subscribe to in order to get notifications from their circle.
- login: Authenticates a user and returns circle data to the client.
- register: Registers a user into the database.
- circle join: Adds a user to a circle.
- circle create: Creates a circle.
- publishRegistrationToken: Sends a Firebase Device token to the server initially and whenever there is another one, used for targeted messaging, not topic messaging.
## Communication
The mobile app communicates with the server using gRPC too. If a switch to rest ever needs to be made, neither the server nor mobile need to change, because a REST grpc-gateway proxy can be attached to another port, forwarding all the requests. My own example: [grpc-gateway example](
https://github.com/adiprerepa/grpc-gateway-example).
- EDIT 1/18/20 - Finished This. The proxy runs on port `8081`, which connects to the localhost java server running on port `2001`. When connecting via http rest, connect to port 8081, but for gRPC clients, connect directly to the service at port `2001`. The proxy is written in go, and is in `/Gateway`.

## Tech Stack
- Server: Java + maven + gRPC
- App: Java + gRPC
- Proxy: Golang + REST
- Raspberry Pi: Python 2.7 + BLE
### Interesting ideas
- What if there was a service that constantly sent you text messages and emails, and if you did not respond, that would simulate the same thing?
- What if when you activated a lifepod, your phone started recording with the mic events and streamed them to everyone in your circle, to listen in and make sure your are safe? The recordings could also be useful for third-party investigative entities such as the police to apprehend the suspected assailant? (Big words I know)
- Use the accelerometers on the phone in combination with the location to determine if something is happening.
  - Think a LifeAlert but automated.
