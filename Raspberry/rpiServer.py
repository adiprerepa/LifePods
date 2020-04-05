import os
import glob
import time
import RPi.GPIO as gpio
from bluetooth import *

os.system('modprobe w1-gpio')
os.system('modprobe w1-therm')
input_pin = 10
status_pin = 8

gpio.setmode(gpio.BOARD)
gpio.setup(input_pin, gpio.IN)
gpio.setup(status_pin, gpio.OUT)
gpio.setup(input_pin, gpio.IN, pull_up_down=gpio.PUD_DOWN)

#base_dir = '/sys/bus/w1/devices/'
#device_folder = glob.glob(base_dir + '28*')[0]
#device_file = device_folder + '/w1_slave'

gpio.output(status_pin, gpio.LOW)


# Returns if button is high
def read_status():
  return gpio.input(input_pin) == gpio.HIGH

server_sock = BluetoothSocket(RFCOMM)
server_sock.bind(("", PORT_ANY))
server_sock.listen(1)

port = server_sock.getsockname()[1]

uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"

advertise_service(server_sock, "LifePodServer", service_id = uuid, service_classes = [uuid, SERIAL_PORT_CLASS], profiles = [SERIAL_PORT_PROFILE],)

"""
while True:
  print(read_status())
"""

while True:
  print "Waiting for connection on RFCOMM channel %d" % port
  client_sock, client_info = server_sock.accept()
  gpio.output(status_pin, gpio.HIGH)
  print "Accepted connection from ", client_info
  try:
    data = client_sock.recv(1024)
    if len(data) == 0: break
    print "recieved [%s]" % data
    if data == 'threatStatus':
      print(read_status())
      if read_status():
        data = "T"
      else:
        data = "F"
    else:
      data = 'unknownd'
    client_sock.send(data)
    gpio.output(status_pin, gpio.LOW)
    print "sending [%s]" % data
  
  except IOError:
    pass

  except KeyboardInterrupt:
    gpio.output(status_pin, gpio.LOW)
    print "disconnected"
    client_sock.close()
    server_sock.close()
    print "all done"
    break

