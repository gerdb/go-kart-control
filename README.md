#Go-Kart Control  
####Radio controlled displays for go-kart tracks  
---
 
![slpash](https://raw.githubusercontent.com/gerdb/go-kart-control/master/java/src/resources/splash.png)


###Screenshot of the java application
![screenshot](https://raw.githubusercontent.com/gerdb/go-kart-control/master/pics/screenshot1.png)


###Install the Go-Kart-Control software
Download and unzip the ZIP archive containing all executables and also the source code:  
https://github.com/gerdb/go-kart-control/archive/master.zip  
On a windows system just start the executable **Go-Kart-Control.exe** in /install/win32 or win64  
On a Mac or Linux start the java JAR archive in install/all with **java - jar go-kart-control.jar**.  
Don't forget to set the correct serial port in settings.txt.  

###Translation
You can translate the application.  
Open the *lang/messages.pot* file and translate it with http://poedit.net/  
Save the *messages_LANGUAGE_CODE.po* file in the *lang* folder of your program.  
Set the local code in *settings.txt*, if you don't want to use the system settings.

###Arduino software
If it's a new AVR, program it with the Arduino bootloader. For example with AVRDUDE:  
`avrdude -pm32u4 -cstk500 -P/dev/ttyUSB0 -e`  
`avrdude -pm32u4 -cstk500 -P/dev/ttyUSB0 -v -e -U lfuse:w:0xff:m -U hfuse:w:0xd8:m -U efuse:w:0xcb:m`  
`avrdude -pm32u4 -cstk500 -P/dev/ttyUSB0 -v -e -U flash:w:Caterina-Micro.hex -U lock:w:0x2F:m`

The Arduino project file is: **arduino/go_kart_panel/go_kart_panel.ino**

###Hardware setup
The PCB can be assembles in 2 variants: As a master module or as a panel module.  
The master module is connected to the PC.  
The panel module has a display.



###PCB layout
![screenshot](https://raw.githubusercontent.com/gerdb/go-kart-control/master/pics/pcb1.jpg)