#include <SoftwareSerial.h>

SoftwareSerial mySerial(2, 3);

void setup()  
{
 /* Serial.begin(115200);
  Serial.println("Hello Roomba");
  pinMode(2, INPUT);
  pinMode(3, OUTPUT);
  pinMode(0, INPUT);
  pinMode(13, OUTPUT); */
  // set the data rate for the SoftwareSerial port
  
  Serial.begin(115200);
  Serial.write((byte)128);
  
  Serial.write((byte)135);
}

void loop() // run over and over
{
  /*while (Serial.available()) {
    Serial.println(Serial.read());
  }
  digitalWrite(13, HIGH);
  mySerial.write((byte)137);
  mySerial.write((byte)0);
  mySerial.write((byte)100);
  mySerial.write((byte)0);
  mySerial.write((byte)1);
  delay(10000);
  digitalWrite(13, LOW);
  mySerial.write((byte)137);
  mySerial.write((byte)0);
  mySerial.write((byte)0);
  mySerial.write((byte)0);
  mySerial.write((byte)0);
  delay(1000);*/
}
