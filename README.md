# Android-Car

Client:
    Android mobile app in app directory
Server: 
    Motor driver controlled by ESP8266 in socketsever directory

Hardware Description: 
    The PWM output mode of ESP8266 Pin1 controls the speed of the left front wheel and left rear wheel through the motor driver chip
    The PWM output mode of ESP8266 Pin2 controls the speed of the right front wheel and right rear wheel through the motor driver chip
    The GPIO mode of ESP8266 Pin6/Pin5 controls the wheel forward and reverse through the motor driver chip

Tips: 
    The back-EMF will be generated when the motor is switched, and the motor drive chip may be burned in severe cases, so the time delay of the motor steering has been done in the software. In addition, the welding direction breakdown voltage between the motor drive chip and the motor is recommended to be 50V. Schottky diode above
