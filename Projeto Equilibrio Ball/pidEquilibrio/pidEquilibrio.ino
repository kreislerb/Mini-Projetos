#include "SoftwareSerial.h"
#include "Servo.h"
//SERVO DE 0 a 90
//ONDE A REFERENCIA ESTA EM 45° ou seja o ponto de equilibrio
/***********************************************************************************************/
/********************* CONFIGURAÇÃO DOS PINOS **************************************************/
#define  Servo_1_Pin  10 // Pino PWM ligado ao Servo Motor 1 ->  Posição em vista superior: INFERIOR ESQUERDO
/*#define potKp 5
#define potKi 4
#define potKd 3
#define button 7*/
/* Definição de um objeto SoftwareSerial.
 * Usaremos os pinos 8 e 9, como RX e TX, respectivamente.
 */
SoftwareSerial serial(2, 3);
void calculatePID();
void refreshParams(float _sp, float _kp, float _ki, float _kd);
int limitador(float value);
void clearParams();

/***********************************************************************************************/
/********************* Declaração de Variáveis ****************************************/

float valorPotKp = 0;
float valorPotKd = 0;
float valorPotKi = 0;


//MOTORS
Servo Servo1;

//BLUETOOTH
String buffAndroid = "";
//PC TUNNING
String buffTunningS = "";
char buffTunning[40];
float ctt[4];

//BALL
float ballPosX = 0;
int ballPosition= 0;


//CONTROLE
float Setpoint, Input =0, Output = 0;
int maximo = 120;
int minimo = 60;

float P =0, I=0, D=0, error = 0, previousError = 0;
float kp= 0.01, ki=0.01, kd=0.01;

/***********************************************************************************************/
/***********************************************************************************************/

void setup() {
  
  Serial.begin(19200);
  serial.begin(9600);
  Servo1.attach(Servo_1_Pin);
  Servo1.write(90);
 
  Setpoint = 120;
 /* pinMode(potKp, INPUT);
  pinMode(potKi, INPUT);
  pinMode(potKd, INPUT);
  pinMode(button, INPUT_PULLUP);*/
  //turn the PID on
  delay(500);

}

void loop() {

 /*Comunicação com o Pc Tunning
 A informação esperada desta porta são as constantes do PID + SetPoint no seguinte formato: "SP;KP;KI;KD"*/
 
  if (Serial.available())
  { 
    int cont = 0;
    int ref = 0;
       while(Serial.available())
       {
         buffTunningS += (char)Serial.read(); 
         delay(2);
        
       } 
       buffTunningS.toCharArray(buffTunning, 40);
       
       for(int i = 0; i<buffTunningS.length();i++){
           if(buffTunning[i]== ';'){
               if(ref==0){
                 ctt[cont] =  (buffTunningS.substring(ref,i).toFloat())/10000;
               }
               if(ref>0){
                 ctt[cont] = (buffTunningS.substring(ref+1,i).toFloat())/10000;
               }
               ref = i;
               cont++;
             
           }
       }
       refreshParams(ctt[0], ctt[1], ctt[2], ctt[3]);
       clearParams();
       buffTunningS = "";
        /*
       Serial.print("Sp: ");
       Serial.print(ctt[0],4);
       Serial.print("\t");
       Serial.print("Kp: ");
       Serial.print(ctt[1],4);
       Serial.print("\t");
       Serial.print("Ki: ");
       Serial.print(ctt[2],4);
       Serial.print("\t");
       Serial.print("Kd: ");
       Serial.println(ctt[3],4);
   
 
      */
    }
      
/*
if(!digitalRead(button)){
   for(int i = 0; i<25; i++){ 
   valorPotKp += analogRead(potKp);
   valorPotKi += analogRead(potKi);
   valorPotKd += analogRead(potKd);
   delay(2);
   }
  valorPotKp /= 25;
  valorPotKi /= 25;
  valorPotKd /= 25;
  kp = 0.001*(valorPotKp);
  ki = 0.001*(valorPotKi);
  kd = 0.001*(valorPotKd);
  clearParams(); 
   Serial.print("kp: ");
   Serial.print(kp,4);
   Serial.print("\t");
   Serial.print("ki: ");
   Serial.print(ki,4);
   Serial.print("\t");
   Serial.print("kd: ");
   Serial.println(kd,4);
}
*/ 
  
  
  
  /*Comunicação Bluetooth Android
  A informação esperada desta porta é a posição da bola captada pela camera do smartphone*/
  if (serial.available())
  { 
       while(serial.available())
       {
         buffAndroid += (char)serial.read(); 
         
       } 
     ballPosX = buffAndroid.toFloat();
     ballPosition = (int) ballPosX;
     // Serial.print("Bluetooth: ");
     //Serial.println(ballPosition); 
     buffAndroid = ""; 
     int angulo = 0;
     Input = ballPosition;
     calculatePID();
     angulo = (int) (90-Output);
     angulo = limitador(angulo);
     Servo1.write(angulo);
     
     Serial.print(ballPosition);
     Serial.print(";");
     Serial.println(angulo);
     
    
  
   /*
       Serial.print("Sp: ");
       Serial.print(Setpoint);
       Serial.print("\t");
       Serial.print("Kp: ");
       Serial.print(kp);
       Serial.print("\t");
       Serial.print("Ki: ");
       Serial.print(ki);
       Serial.print("\t");
       Serial.print("Kd: ");
       Serial.println(kd);*/
     /*
     Serial.print("Output: ");
     Serial.print(Output);
     Serial.print("\t");
     Serial.print("P: ");
     Serial.print(P);
     Serial.print("\t");
     Serial.print("I: ");
     Serial.print(I);
     Serial.print("\t");
     Serial.print("D: ");
     Serial.print(D);
     Serial.print("\t");
     Serial.print("Error: ");
     Serial.println(error);*/
   } 
   
} 
   
void calculatePID()
{
  error = Setpoint - Input;
  P = error; 
  I += error; 
  if(I> 100000 || I<-100000){
    I=0;
  }
  D = error-previousError;
  Output = (kp*P) + (ki*I) + (kd*D);
  previousError = error;
}

void refreshParams(float _sp, float _kp, float _ki, float _kd){
   Setpoint = _sp;
   kp = _kp;
   ki=_ki;
   kd= _kd;
}

int limitador(int value){
  if(value<=minimo){
    return minimo;
  }
  if(value>=maximo){
    return maximo;
  }
  
}

void clearParams(){
  P = 0;
  I = 0;
  D = 0; 
  error = 0;
  previousError = 0;
}
  /////FIM DA COMUNICAÇÃO COM ANDROID


 
  
  






