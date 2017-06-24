# Kojak Scanner Websocket Streamer.

About the scanner:
https://integratedbiometrics.com/es/products/kojak/
![N|Solid](https://integratedbiometrics.com/wp-content/uploads/2016/01/FBI-Appendix-F-Certified-FAP-60-10-Print-Roll-Scanner.jpg)


On this java webapp, you can connect the scanner from the host machine, to the browser. Ideal for kiosks, IoT devices, worstations, etc.

You can use image autocapture, or you can use a button to trigger snapshot. On our implementation, the button was a switch, conected to an anrduino nano, via USB, and handled by node-red. When the switch was pressed, node triggers a websocket message handled by the server, and sent to the scanner.
A diagram and the code of the arduino will be eventually updated.

Runs on tomcat, therefore it can be executed anywherwe it can be installed!

The faster the CPU, the faster de FPS.

Raspberry ~10fps
i3 Laptop ~30fps
macbook pro ~a lot of fps
  


