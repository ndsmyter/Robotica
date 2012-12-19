#include <SoftwareSerial.h>

int rxPin = 11;
int txPin = 10;
int ddPin = 12;
SoftwareSerial roomba(rxPin, txPin); // RX, TX

//sensor powerOn
int s0Pin = 3;
int s1Pin = 4;
int s2Pin = 5;
int s3Pin = 6;
int s4Pin = 7;

//debug Led
int redPin = 5;
int grePin = 6;
int yelPin = 7;
int enable = 8;

void setup()  
{
  pinMode(ddPin,  OUTPUT);   // sets the pins as output
  digitalWrite(ddPin, LOW);
  pinMode(redPin, OUTPUT);   // sets the pins as output
  digitalWrite(redPin, LOW);
  pinMode(grePin, OUTPUT);   // sets the pins as output
  digitalWrite(grePin, LOW);
  pinMode(yelPin, OUTPUT);   // sets the pins as output
  digitalWrite(yelPin, LOW);
  pinMode(enable, INPUT);
  digitalWrite(enable, LOW);
  pinMode(s0Pin, OUTPUT);
  digitalWrite(s0Pin, LOW);
  pinMode(s1Pin, OUTPUT);
  digitalWrite(s1Pin, LOW);
  pinMode(s2Pin, OUTPUT);
  digitalWrite(s2Pin, LOW);
  pinMode(s3Pin, OUTPUT);
  digitalWrite(s3Pin, LOW);
  pinMode(s4Pin, OUTPUT);
  digitalWrite(s4Pin, LOW);
    
  roomba.begin(19200);
  Serial.begin(9600);
  
  //Serial.write((byte)0);
  //Serial.write((byte)0);
  //Serial.write((byte)14);
  //Serial.print("Roomba Startup");
  digitalWrite(redPin, HIGH); // say we're alive
  
  // Baud rate change
  //Serial.write((byte)0);
  //Serial.write((byte)0);
  //Serial.write((byte)16);
  //Serial.print("Baud rate change");
  digitalWrite(ddPin, LOW);
  delay(100);
  digitalWrite(ddPin, HIGH);
  delay(2000);
  for(int i=0; i<3; i++){
    digitalWrite(ddPin, LOW);
    digitalWrite(redPin, LOW);
    delay(250);
    digitalWrite(ddPin, HIGH);
    digitalWrite(redPin, HIGH);
    delay(250);
  }
  
  //Serial.write((byte)0);
  Serial.write((byte)0);
  Serial.write((byte)12);
  Serial.print("Roomba Ready");
  digitalWrite(redPin, LOW);
}

void loop() // run over and over
{
  // van de roomba voor de pc
  if (roomba.available()){
    Serial.write((byte)1);
    Serial.write(roomba.read()); //header (altijd = 19)
    int lengte = roomba.read();
    Serial.write(lengte);
    for (int i=0; i<lengte; i++){
      Serial.write(roomba.read());
    }
    Serial.write(roomba.read()); //checksum
  }
  
  //van de pc voor de roomba (misschien)
  if (Serial.available()){
    if(analogRead(0) > 0){
      digitalWrite(grePin, HIGH);
      int aantal = Serial.read();
      int timeout = 8;
      while(Serial.available() < aantal && timeout > 0){
        digitalWrite(grePin, LOW);
        delay(50);
        digitalWrite(grePin, HIGH);
        delay(50);
        timeout --;
      }
      if(timeout > 0){
        int header = Serial.read();
        if(header != 120){
          roomba.write(header);
          for (int i=1; i<aantal; i++){
            roomba.write(Serial.read());
          }
          //bevestig ontvangst pakket
          Serial.write((byte)0);
          Serial.write((byte)3);
          Serial.print("ACK");
        } else {
          digitalWrite(yelPin, HIGH);
          //bevestig ontvangst pakket
          Serial.write((byte)0);
          /*if(header < 0)
            header += 128;
          if(header < 10)
            Serial.write((byte)5);
          if(header < 100 && header >= 10)
            Serial.write((byte)6);
          if(header < 1000 && header >= 100)
            Serial.write((byte)7);
          Serial.print("ACL ");
          Serial.print(header);*/
          Serial.write((byte)3);
          Serial.print("ACK");
          //Stuur antwoord terug
          aantal = Serial.read();
          Serial.write((byte)1);
          Serial.write((byte)header);
          Serial.write((byte)aantal);
          for(int i=0; i<aantal; i++){
            Serial.write(getSensorData(Serial.read()));
          }
          digitalWrite(yelPin, LOW);
        }
      } else {
        digitalWrite(redPin, HIGH);
        while(Serial.available() > 0){
          Serial.read();
        }
        delay(50);
        digitalWrite(redPin, LOW);
      }
      digitalWrite(grePin, LOW);
    } else {
      Serial.read(); //Weggooien
    }
  }
}

byte getSensorData(int s){
  digitalWrite(s0Pin + s, HIGH);
  delay(50);
  int val = analogRead(s);
   digitalWrite(s0Pin + s, LOW);
  byte ret = (byte)map(val, 0, 1023, 0, 255);
  return ret;
}

