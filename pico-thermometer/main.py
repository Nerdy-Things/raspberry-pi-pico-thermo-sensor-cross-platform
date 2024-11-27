# Author: Eugene Tkachenko https://www.youtube.com/@itkacher
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# See the LICENSE file for the full text of the license.

from time import sleep
from itk_pico.wifi import WiFi
from itk_pico.temperature import TemperatureSensor
from itk_pico.http import Http
from itk_pico.logger import Logger
from itk_pico.udp import Udp
from itk_pico.ip_utils import get_broadcast_address
import json

Logger.set_enabled(True)

UDP_PORT = 5468

wifi = WiFi()
wifi.connect("SSID", "Password") # Enter your credentials

temperature_sensor = TemperatureSensor(15)

mac_address = wifi.get_mac_address()
udp = Udp(UDP_PORT)
    
while True:
    wifi.try_reconnect_if_lost()
    temperature = temperature_sensor.get_temperature()
    broadcast_ip = get_broadcast_address(wifi.get_ip_address(), wifi.get_subnet_mask())
    message_dict = {"name": "Kitchen" ,"temperature": temperature, "mac_address": mac_address}
    message = json.dumps(message_dict)
    udp.send(message, broadcast_ip, UDP_PORT)
    sleep(5)
