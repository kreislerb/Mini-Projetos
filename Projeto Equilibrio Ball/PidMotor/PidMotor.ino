#include <PID_v1.h>
#include "Servo.h"
]
#define Servo1Pin 5
#define pot A5
int mediaMovel(int numAmostras);
double Setpoint, Input, Output, kp, ki, kd;
PID myPID(&Input, &Output, &Setpoint,1,0.1,0.1, DIRECT);
Servo Servo1;



void setup() {
  pinMode(pot, INPUT);
  Serial.begin(9600);
  Servo1.attach(Servo1Pin);
  Setpoint = 90;
  Input = Servo1.read();
  kp = 1; ki = 0.1; kd = 0.1;
  myPID.SetTunings (kp, ki, kd);
  myPID.SetOutputLimits(0, 180);
}

void loop() {
  // put your main code here, to run repeatedly:
  
  int angle = Servo1.read();
  int media = mediaMovel(50);
  Setpoint = media;
  Input = angle;
  myPID.Compute();
  Servo1.write(Output);
  Serial.print("Angulo Lido: ");
  Serial.print(angle);
  Serial.print("  Media: ");
  Serial.print(media);
  Serial.print("Output: ");
  Serial.println(Output);
  

}


int mediaMovel(int numAmostras){
  
  float acum = 0;
  for(int i = 0; i<numAmostras; i++){
  double posPot = analogRead(pot);
  posPot = posPot/5.7;
  acum  = acum+posPot;
  delay(.1);
  }
  acum = acum/numAmostras;
  int angulo = (int) acum;
  return angulo;
}


