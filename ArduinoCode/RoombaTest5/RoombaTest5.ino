  #include <SoftwareSerial.h>
  
  int rxPin = 11;
  int txPin = 10;
  int ddPin = 12;
  SoftwareSerial roomba(rxPin, txPin); // RX, TX
  
  //sensor powerOn
  int s0Pin = 2;
  int s1Pin = 3;
  int s2Pin = 4;
  int s3Pin = 5;
  int s4Pin = 6;
  
  //debug Led
  //int redPin = 13;
  //int grePin = 6;
  int yelPin = 13;
  int enable = 8;
  
  int val[5];
  short ret[5];
  int in[5];
            
  
  
  void setup()  
  {
    pinMode(ddPin,  OUTPUT);   // sets the pins as output
    digitalWrite(ddPin, LOW);
    /*pinMode(redPin, OUTPUT);   // sets the pins as output
    digitalWrite(redPin, LOW);
    pinMode(grePin, OUTPUT);   // sets the pins as output
    digitalWrite(grePin, LOW);*/
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
    
    digitalWrite(yelPin, HIGH); // say we're alive
    
    digitalWrite(ddPin, LOW);
    delay(100);
    digitalWrite(ddPin, HIGH);
    delay(2000);
    for(int i=0; i<3; i++){
      digitalWrite(ddPin, LOW);
      digitalWrite(yelPin, LOW);
      delay(250);
      digitalWrite(ddPin, HIGH);
      digitalWrite(yelPin, HIGH);
      delay(250);
    }
    digitalWrite(yelPin, LOW);
  }
  
  void loop() // run over and over
  {
    // van de roomba voor de pc
    //NOT implemented yet
    
    //van de pc voor de roomba
    if (Serial.available()){
      //digitalWrite(grePin, HIGH);
        int aantal = Serial.read();
        int timeout = 8;
        while(Serial.available() < aantal && timeout > 0){
          //digitalWrite(grePin, LOW);
          delay(50);
          //digitalWrite(grePin, HIGH);
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
          } else {
            digitalWrite(yelPin, HIGH);
            aantal = Serial.read();
            Serial.write((byte)1);
            Serial.write((byte)header);
            Serial.write((byte)(aantal * 2));
            for(int i=0; i<aantal; i++){
              in[i] = Serial.read();
            }
            // Get SensorData
            delay(10);
            for(int i=0; i<8; i++){
              delay(20 - aantal);
              for(int j=0; j<aantal; j++){
                val[j] += analogRead(in[j]);
                delay(1);
              }
            }
            for(int j=0; j<aantal; j++){
              val[j] = val[j] >> 3;
              ret[j] = (short)constrain(val[j], 0, 1024);
              Serial.write((byte)(ret[j]>>8));
              Serial.write((byte)(ret[j]&0xFF));
              val[j] = 0;
            }
            //END 
            digitalWrite(yelPin, LOW);
          }
        } else {
          //digitalWrite(redPin, HIGH);
          while(Serial.available() > 0){
            Serial.read();
          }
          delay(50);
          //digitalWrite(redPin, LOW);
        }
        //digitalWrite(grePin, LOW);
      }
    }

