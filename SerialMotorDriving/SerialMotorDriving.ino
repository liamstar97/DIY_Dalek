/*
  Serial Event example

  When new serial data arrives, this sketch adds it to a String.
  When a newline is received, the loop prints the string and clears it.

  A good test for this is to try it with a GPS receiver that sends out
  NMEA 0183 sentences.

  NOTE: The serialEvent() feature is not available on the Leonardo, Micro, or
  other ATmega32U4 based boards.

  created 9 May 2011
  by Tom Igoe

  This example code is in the public domain.

  http://www.arduino.cc/en/Tutorial/SerialEvent
*/

#include "MotorDriver.h"
int ledPin = 13;
int leftMotor = 0;
int rightMotor = 1;
int currentSpeeds[] = { 0, 0 };

MotorDriver motor;

// Declare the Servo pin 
int servoPin = 3; 

String inputString = "";         // a String to hold incoming data
boolean stringComplete = false;  // whether the string is complete

void setup() {
  // initialize serial:
  Serial.begin(9600);
  // reserve 200 bytes for the inputString:
  inputString.reserve(200);
  motor.begin();
//  pinMode(ledPin, OUTPUT);
}


void drive(int motorNum, int newSpeed) {
  if (newSpeed == 0) {
    currentSpeeds[motorNum] = 0;
    motor.brake(motorNum);
  } else if ((newSpeed > 0 && currentSpeeds[motorNum] >= 0) || (newSpeed < 0 && currentSpeeds[motorNum] <= 0)) {
    currentSpeeds[motorNum] = newSpeed;
    motor.speed(motorNum, newSpeed);
  } else {
    motor.brake(motorNum);
    delay(1000);
    currentSpeeds[motorNum] = newSpeed;
    motor.speed(motorNum, newSpeed);
  }
}

void brakeBoth() {
  motor.brake(leftMotor);
  motor.brake(rightMotor);
}

void driveBoth(int newSpeed) {
  drive(leftMotor, newSpeed);
  drive(rightMotor, newSpeed);
}


void timedBlink(int millis) {
  digitalWrite(ledPin, HIGH);   // turn the LED on (HIGH is the voltage level)
  delay(millis);              // wait for a second
  digitalWrite(ledPin, LOW);    // turn the LED off by making the voltage LOW
  delay(500);              // wait for a second  
}

void dit() { timedBlink(100); }
void dash() { timedBlink(1000); }

void sayS() { dit(); dit(); dit(); }
void sayO() { dash(); dash(); dash(); }

void loop() {
  // print the string when a newline arrives:
  if (stringComplete) {
    Serial.println("You sent: " + inputString);
    inputString.toLowerCase();
    if (inputString.startsWith("drive")) {
      String driveStr = inputString.substring(6);
      String speedTimeStr = driveStr.substring(1);
      speedTimeStr.trim();
      int newSpeed = speedTimeStr.toInt();
      if (driveStr.startsWith("l")) {
        drive(leftMotor, newSpeed);
        Serial.println("Drive left: " + speedTimeStr);
      } else if (driveStr.startsWith("r")) {
        drive(rightMotor, newSpeed);
        Serial.println("Drive right: " + speedTimeStr);
      } else if (driveStr.startsWith("b")) {
        Serial.println("Driving: " + speedTimeStr);
        driveBoth(newSpeed);
      } else {
        Serial.println("I cannae do that, Cap'n! + \n\t[" + driveStr + "]");
      }
    } else {
      Serial.println("ERROR\n");
/*
      sayS();
      sayO();
      sayS();
*/
      delay(2000);
    }
    // clear the string:
    inputString = "";
    stringComplete = false;
  }
}
/*
  SerialEvent occurs whenever a new data comes in the hardware serial RX. This
  routine is run between each time loop() runs, so using delay inside loop can
  delay response. Multiple bytes of data may be available.
*/
void serialEvent() {
  while (Serial.available()) {
    // get the new byte:
    char inChar = (char)Serial.read();
    // add it to the inputString:
    inputString += inChar;
    // if the incoming character is a newline, set a flag so the main loop can
    // do something about it:
    if (inChar == '\n') {
      stringComplete = true;
    }
  }
}
