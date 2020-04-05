# Following setup ran and worked twice on RPI B+
# Raspbian GNU/Linux 10 (buster)
# install required packages
echo "Installing required packages..."
sudo apt-get install bluez python-bluez
# python 2.7 is already installed
# put hci0 card into piscan mode
echo "Putting RPI into hci0 scanning mode"
sudo hciconfig hci0 piscan
echo "Setting device name to 'LifePodDevice'"
# Device NEEDS to be named 'LifePodDevice', the mobile
# app pairs based on name.
sudo hciconfig hci0 name 'LifePodDevice'
# This is where things get tricky
# I have not tested whether you NEED bluetoothctl,
# but I used it and it works for me. You need to pair the
# raspberry pi with the mobile device, and that is done
# through bluetoothctl. You cannot execute commands for bluetoothctl
# from a script, so I am going to put the instructions here.
# EDIT: You can use coprocesses to interact with bluetoothctl from
# a script, todo in the future.
# To go into bluetoothctl, type:
# $ sudo bluetoothctl
# To register an agent, run:
# $ [bluetoothctl]  agent on
# $ [bluetoothctl] default-agent
# $ [bluetoothctl] discoverable on
# To list available devices:
# $ [bluetoothctl] device list
# Find the device, and it's MAC address
# $ [bluetoothctl] pair <MAC>
# A pairing prompt screen should come up on the phone.
# To trust the device: 
# $ [bluetoothctl] trust <MAC>
# that should be it.
# Now, we need to echo an argument into /etc/systemd/system/dbus-org.bluez.service
# to put it in compatibility mode.
# DOES NOT WORK
#sed 's/# ExecStart=/usr/lib/bluetooth/bluetoothd/ExecStart=/usr/lib/bluetooth/bluetoothd -C/' /etc/systemd/system/dbus-org.bluez.service
# Manually do it for now.
# Add Serial Port Profile
sudo sdptool add SP
# Restart bluetooth
sudo systemctl daemon-reload
sudo service bluetooth restart 
# now run the script and advertise the service
sudo python rpiServer.py
