#include <WiFi.h>
#include "FirebaseESP32.h"
#include "max6675.h"                                       //INCLUSÃO DE BIBLIOTECAS
 
int SO = 25;                                               //PINO DIGITAL (SO)
int CS = 33;                                               //PINO DIGITAL (CS)
int CLK = 32;                                              //PINO DIGITAL (CLK / SCK)
int Sensorfumaca = 35;                                     //PINO DIGITAL UTILIZADO PELO SENSOR DE FUMAÇA
int sensorpir1 = 4;                                        //PINO DIGITAL UTILIZADO PELO SENSOR DE PRESENÇA 1
int sensorpir2 = 13;                                       //PINO DIGITAL UTILIZADO PELO SENSOR DE PRESENÇA 2
int valvula = 23;                                          //PINO DIGITAL UTILIZADO PELA VALVULA
int buzzer = 15;
int tempo;
int i;

MAX6675 sensor(CLK, CS, SO);                               //CRIA UMA INSTÂNCIA UTILIZANDO OS PINOS (CLK, CS, SO)

  
// Configure com suas credenciais
#define FIREBASE_HOST "alert-fire-default-rtdb.firebaseio.com"
#define FIREBASE_AUTH "JK8YRJKP8hX9****b38woCQQhvYHi1LtuZM"
#define WIFI_SSID "****"
#define WIFI_PASSWORD "***" 
        
void setup() {
  
  pinMode(Sensorfumaca, INPUT);                                                                      //DEFINE O PINO COMO ENTRADA
  pinMode(sensorpir1, INPUT);                                                                        //DEFINE O PINO COMO ENTRADA
  pinMode (sensorpir2, INPUT);                                                                       //DEFINE O PINO COMO ENTRADA
  pinMode (valvula, OUTPUT);                                                                         //DEFINE O PINO COMO SAIDA
  pinMode (buzzer, OUTPUT);
  
  Serial.begin(115200);                                                                              //Inicia o monitor serial          

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);                                                              //Rotina pra conectar ao wifi.
  Serial.print("conectando");
  
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
  Serial.println();
  Serial.print("conectado: ");
  Serial.println(WiFi.localIP());
 
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);                                                       //Iniciar Firebas
 }

FirebaseData database;

void loop() {
 //Serial.print("Temperatura: ");                                                                     
 //Serial.println(sensor.readCelsius());                                                              
 //delay(200);  
                                                                                  
  while (Firebase.getBool(database, "/FireAlert/auto")==0){
    if(Firebase.getBool(database, "/FireAlert/valvula")==true){
      digitalWrite(valvula,HIGH);
      }
    if(Firebase.getBool(database, "/FireAlert/valvula")==0){
      digitalWrite(valvula,LOW);}
  }

  if(analogRead(Sensorfumaca)==0){                                                             
    Firebase.setBool(database, "/FireAlert/valvula", true);
     while (Firebase.getBool(database, "/FireAlert/valvula")==1){
         Firebase.setBool(database, "/FireAlert/note", true);
         digitalWrite(valvula, HIGH);                                                                  
         Serial.println ("VALVULA LIGADAs");                                                          
    } 
  }
  digitalWrite(valvula, LOW);
  Firebase.setBool(database, "/FireAlert/note", false);
      
 if((Firebase.getBool(database, "/FireAlert/auto")==1) && sensor.readCelsius() >= 50){
     
       while((digitalRead(sensorpir1) == 0) && (digitalRead(sensorpir2) == 0)){               
             digitalWrite(valvula,HIGH);
             Firebase.setBool(database, "/FireAlert/note", 1);     
             digitalWrite(buzzer, LOW);                                                                                                 
         } 
      
        while(((digitalRead(sensorpir1)==1) || (digitalRead(sensorpir2)==1)) && Firebase.getBool(database, "/FireAlert/tdefine")==1) {  
       //Serial.println("foi");
       //Serial.println(digitalRead(sensorpir1));
       //Serial.println(digitalRead(sensorpir2));
       //delay(400);  
       digitalWrite(valvula, LOW);
        tempo = Firebase.getInt(database, "/FireAlert/time");
        tempo = (tempo*60000);
        delay(tempo);
        digitalWrite(buzzer, HIGH);
        Firebase.setBool(database, "/FireAlert/note3",true);
         Firebase.setBool(database, "/FireAlert/note3", true); // notificação de fim de tempo
            while(digitalRead(sensorpir1)==1) {
               digitalWrite(buzzer, LOW);
               Firebase.setBool(database, "/FireAlert/note3",false);
               Firebase.setInt(database, "/FireAlert/tdefine", 0);  
               }
        }
   }
  
 Firebase.setBool(database, "/FireAlert/note", false);
}  
