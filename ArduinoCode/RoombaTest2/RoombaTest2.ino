  /*
   * RoombaBumpTurn 
   * --------------
   * Implement the RoombaComm BumpTurn program in Arduino
   * A simple algorithm that allows the Roomba to drive around 
   * and avoid obstacles.
   * 
   * Arduino pin 0 (RX) is connected to Roomba TXD
   * Arduino pin 1 (TX) is connected to Roomba RXD
   * Arduino pin 2      is conencted to Roomba DD
   * 
   * Updated 20 November 2006
   * - changed Serial.prints() to use single print(v,BYTE) calls instead of 
   *    character arrays until Arduino settles on a style of raw byte arrays
   *
   * Created 1 August 2006
   * copyleft 2006 Tod E. Kurt <tod@todbot.com>
   * http://hackingroomba.com/
   */
  
  #include <SoftwareSerial.h>
  
  int rxPin = 11;
  int txPin = 10;
  int ddPin = 2;
  SoftwareSerial roomba(rxPin, txPin); // RX, TX
  
  int ledPin = 13;
  char sensorbytes[10];
  
  #define bumpright (sensorbytes[0] & 0x01)
  #define bumpleft  (sensorbytes[0] & 0x02)
  
  void setup() {
  //  pinMode(txPin,  OUTPUT);
    pinMode(ddPin,  OUTPUT);   // sets the pins as output
    pinMode(ledPin, OUTPUT);   // sets the pins as output
    
    roomba.begin(19200);
    Serial.begin(9600);
  
    digitalWrite(ledPin, HIGH); // say we're alive
  
    // Baud rate change
    Serial.println("Baud rate change");
    digitalWrite(ddPin, LOW);
    delay(100);
    digitalWrite(ddPin, HIGH);
    delay(2000);
    for(int i=0; i<3; i++){
      digitalWrite(ddPin, LOW);
      digitalWrite(ledPin, LOW);
      delay(250);
      digitalWrite(ddPin, HIGH);
      digitalWrite(ledPin, HIGH);
      delay(250);
    }
    // set up ROI to receive commands  
    roomba.write((byte)0x80);  // START
    Serial.println("Start");
    delay(1000);
    roomba.write((byte)0x83);  // Safe
    Serial.println("Safe");
    delay(1000);
    //roomba.write((byte)0x8F);  // Seek Dock
    //Serial.println("Seek Dock");
    //song();
    //leds();
    digitalWrite(ledPin, LOW);  // say we've finished setup
  }
  
  void loop() {
    if(analogRead(0) > 0){
      digitalWrite(ledPin, HIGH); // say we're starting loop
      goForward();
      delay(10000);
      digitalWrite(ledPin, LOW);  // say we're after updateSensors
      stoppen();
      delay(1000);
      digitalWrite(ledPin, HIGH); // say we're starting loop
      spinRight();
      delay(10000);
      digitalWrite(ledPin, LOW);  // say we're after updateSensors
      stoppen();
      delay(1000);
      digitalWrite(ledPin, HIGH); // say we're starting loop
      goForward();
      delay(10000);
      digitalWrite(ledPin, LOW);  // say we're after updateSensors
      stoppen();
      digitalWrite(ledPin, HIGH); // say we're starting loop
      spinLeft();
      delay(10000);
      digitalWrite(ledPin, LOW);  // say we're after updateSensors
      stoppen();
      delay(1000);
    }
    delay(1000);
  }
  
  void song() {
    Serial.write(140);
    Serial.write(1);
    Serial.write(1);
    Serial.write(69);
    Serial.write(100);
    delay(3000);
    Serial.write(141);
    Serial.write(1);
  }
  
  void leds() {
    Serial.write((byte)0x8B);  
    Serial.write((byte)0x01);   
    Serial.write((byte)0x01);
    Serial.write((byte)0x01);
  }
  
  void goForward() {
    Serial.println("Go Forward");
    roomba.write((byte)0x89);   // DRIVE
    roomba.write((byte)0x00);   // 0x00c8 == 200
    roomba.write((byte)0x32);
    roomba.write((byte)0x80);
    roomba.write((byte)0x00);
  }
  void stoppen() {
    Serial.println("Stop");
    roomba.write((byte)0x89);   // DRIVE
    roomba.write((byte)0x00);   
    roomba.write((byte)0x00);
    roomba.write((byte)0x00);
    roomba.write((byte)0x00);
  }
  
  void goBackward() {
    Serial.println("Go Backward");
    roomba.write((byte)0x89);   // DRIVE
    roomba.write((byte)0xff);   // 0xff38 == -200
    roomba.write((byte)0xCE);
    roomba.write((byte)0x80);
    roomba.write((byte)0x00);
  }
  void spinLeft() {
    Serial.println("Spin Left");
    roomba.write((byte)0X89);   // DRIVE
    roomba.write((byte)0x00);   // 0x00c8 == 200
    roomba.write((byte)0x32);
    roomba.write((byte)0x00);
    roomba.write((byte)0x01);   // 0x0001 == spin left
  }
  void spinRight() {
    Serial.println("Spin Right");
    roomba.write((byte)0x89);   // DRIVE
    roomba.write((byte)0x00);   // 0x00c8 == 200
    roomba.write((byte)0x32);
    roomba.write((byte)0xff);
    roomba.write((byte)0xff);   // 0xffff == -1 == spin right
  }
  /*void updateSensors() {
    Serial.print(142);
    Serial.print(1,   BYTE);  // sensor packet 1, 10 bytes
    delay(100); // wait for sensors 
    char i = 0;
    while(Serial.available()) {
      int c = Serial.read();
      if( c==-1 ) {
        for( int i=0; i<5; i ++ ) {   // say we had an error via the LED
          digitalWrite(ledPin, HIGH); 
          delay(50);
          digitalWrite(ledPin, LOW);  
          delay(50);
        }
      }
      sensorbytes[i++] = c;
    }    
  }*/
