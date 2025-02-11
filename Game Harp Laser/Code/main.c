/*
%Titulo HARP MUSIC LASER
%Autor: KREISLER BRENNER MENDES
%Finalizado em: 07/12/2017

%Descri��o:
   O HARP MUSIC LASER � uma interface de usu�rio musical eletr�nico e exibi��o de ilumina��o a laser. 
   Projeta v�rios feixes de laser, e um m�sico toca bloqueando-os para produzir sons, que relembram uma harpa.  
%Funcionalidades:
  
   > MENU ROTATIVO COM BOTOES CIMA, BAIXO, ENTER E CANCEL
   > CONTROLE DE MENUS COM INDEXADORES DE N�VEL (EVITA PROBLEMAS COM PILHA)
   > GERENCIA DE DADOS COM INFORMA��ES DAS M�SICAS
   > INDICAR O USUARIO A NOTA A SER TOCADA
   > VERIFICA��O DE ACERTOS DAS NOTAS DA MUSICA JOGADA.
   > CALCULAR PONTUACAO FINAL
   > SALVAR RECORDS NA MEMORIA EEPROM

%Entradas
   PORTA B -> CADA BIT REPRESENTA UM LDR DIGITALIZADO E CADA LDR REPRESENTA UMA NOTA MUSICAL
   PINO E2 -> BOT�O UP
   PINO D0 -> BOT�O DOWN
   PINO C6 -> BOT�O ENTER
   PINO C7 -> BOT�O CLOSE
   Total de pinos como entrada: 12;
%Saidas
   PINOS D1 - D7 -> LCD
   8 LEDS
   Total de pinos como entrada: 15;

   
*/
#include <main.h>
#include <math.h>

#define LCD_ENABLE_PIN PIN_D1
#define LCD_RS_PIN PIN_D3
#define LCD_RW_PIN PIN_D2
#define LCD_DATA4 PIN_D4
#define LCD_DATA5 PIN_D5
#define LCD_DATA6 PIN_D6
#define LCD_DATA7 PIN_D7

#include <lcd.c>

const int8 DO_3 = 0b11111110;
const int8 RE_3 = 0b11111101;
const int8 MI_3 = 0b11111011;
const int8 FA_3 = 0b11110111;
const int8 SOL_3= 0b11101111;
const int8 LA_3 = 0b11011111;
const int8 SI_3 = 0b10111111;
const int8 DO_4 = 0b01111111;
const int8 PAUSA= 0b11111111;

CONST int QUANT_MUSICAS = 4;


int menuPrincipal(); // Menu
int listaMusicas();  // Menu 1
void records();      // Menu 2 - TOP 10 das melhores pontua��es

void carregar(); // Prepara o usu�rio para come�ar a jogar
void notesToLeds(int8 note);  //Acende  o led que corresponde a nota a ser tocada
int tocar_musica(int index); // Retorna a pontua��o dada em percentual
void qualificarPrecisao(int8 index,int8 acertos_nota);//Mostra ao usu�rio o percentual

/*Compara a pontua��o obtida pelo usuario com as armazenadas na mem�ria (TOP 10), caso
a pontua��o seja igual ou maior � pontua�ao comparada, ent�o substitui-se a obtida pela 
comparada e desloca uma posi�ao abaixo todas as outras pontua�oes inferiores.
Esses dados s�o armazenados na memoria EEPROM INTERNA
*/
void salvar(int8 pontuacao,int8 index);  


struct musica {
char titulo[16];
int tempo;
};

struct musica sound[QUANT_MUSICAS];

//VARIAVEIS PARA CONTROLE DO MENU ROTATIVO
int index_Nivel1 = 0;
int index_Nivel2 = 0;
boolean flag_control = 1;
boolean flag_ENTER = FALSE; 
boolean flag_CLOSE = FALSE; 
boolean flag_DOWN = FALSE; 
boolean flag_UP = FALSE; 



void main()
{
   //INSERINDO MUSICAS
   sound[0].titulo = "DO RE MI FA";
   sound[0].tempo = 100;
   
   sound[1].titulo = "ASA BRANCA";
   sound[1].tempo = 120;
   
   sound[2].titulo = "INDIANA JONES";
   sound[2].tempo = 120;
   
   sound[3].titulo = "SYMPHONY No. 9";
   sound[3].tempo = 70;
   //
   
   //CODIGO PARA GRAVAR ALGUNS RECORDS INICIAIS, � executado apenas quando o codigo de programa � gravado, pois a memoria eeprom � resetada para o original.
   int valor = 0;
   valor = read_eeprom(0x00);
   if(valor==255){
   for(int j = 0; j<10;j++){
   write_eeprom(j,20-j);
   }
   write_eeprom(0x0A,0);
   write_eeprom(0x0B,1);
   write_eeprom(0x0C,2);
   write_eeprom(0x0D,0);
   write_eeprom(0x0E,1);
   write_eeprom(0x0F,2);
   write_eeprom(0x10,2);
   write_eeprom(0x11,0);
   write_eeprom(0x12,1);
   write_eeprom(0x13,2);
   }
   //
   
   
   lcd_init();
   animacaoLogo();
   
   
   while(TRUE)
   {
   //CONTROLE DOS MENUS 
      index_nivel1 = menuPrincipal();
      flag_control = true;
      if(index_nivel1 == 1){

         index_nivel2 = listaMusicas(); 
         flag_control = true;
         if(index_nivel2>0){
            carregar();
            int pontuacao =  tocar_musica(index_nivel2-1);
            salvar(pontuacao, index_nivel2-1);
         }
      }
      else{
         records();
         flag_control = true;
      }
   //      
   
   }    
}


void carregar(){
   printf(lcd_putc, "\f");
   lcd_gotoxy(1,1);
   printf(lcd_putc, "  CARREGANDO..");
   ledEffect();
   apagarLeds();
   delay_ms(1000);
   lcd_gotoxy(1,2);
   printf(lcd_putc, "   3..");
   delay_ms(1000);
   printf(lcd_putc, "2..");
   delay_ms(1000);
   printf(lcd_putc, "1..");
   delay_ms(1000);
   lcd_gotoxy(1,1);
   printf(lcd_putc, "\f");
   printf(lcd_putc, "      VAI!!");
 
}


int tocar_musica(int index){

//NOTAS DAS MUSICAS, CADA LINHA REPRESENTA UMA MUSICA
   int8 notas[200] = {DO_3, RE_3, MI_3, FA_3, FA_3, FA_3,      DO_3, RE_3, DO_3, RE_3, RE_3, RE_3,       RE_3, SOL_3, FA_3, MI_3, MI_3,MI_3,     DO_3, RE_3, MI_3, FA_3,   PAUSA,    DO_3, RE_3, MI_3, FA_3, FA_3, FA_3,      DO_3, RE_3, DO_3, RE_3, RE_3, RE_3,       RE_3, SOL_3, FA_3, MI_3, MI_3,MI_3,     DO_3, RE_3, MI_3, FA_3,
                      DO_3, RE_3,    MI_3, SOL_3, SOL_3, MI_3,    FA_3, FA_3, DO_3, RE_3,     MI_3, SOL_3, SOL_3, FA_3,     MI_3, DO_3,DO_3,RE_3,      MI_3,SOL_3,   PAUSA    ,SOL_3,FA_3,MI_3,     DO_3,FA_3,   PAUSA    ,FA_3,MI_3,RE_3,     RE_3,MI_3,RE_3,RE_3,DO_3,DO_3,      SI_3,SOL_3,LA_3,SOL_3,MI_3,FA_3,RE_3,MI_3,RE_3,MI_3,DO_3,RE_3,DO_3,LA_3,DO_3,DO_3,         
                      MI_3,PAUSA, FA_3, SOL_3, DO_4,PAUSA,RE_3,PAUSA,MI_3,       FA_3,PAUSA,SOL_3,PAUSA, LA_3,        SI_3, MI_3,PAUSA,LA_3,PAUSA, SI_3, DO_3,          RE_3, MI_3,PAUSA,MI_3,PAUSA, FA_3, SOL_3, DO_4,       PAUSA,RE_3,PAUSA, MI_3, FA_3,PAUSA,     SOL_3,PAUSA, SOL_3,MI_3,RE_3,PAUSA,SOL_3,MI_3,       RE_3,PAUSA,SOL_3,MI_3,RE_3,PAUSA,SOL_3,MI_3,     RE_3,          
                      MI_3, MI_3, FA_3, SOL_3, SOL_3, FA_3, MI_3, RE_3,    DO_3, DO_3, RE_3, MI_3, MI_3,    PAUSA,   RE_3, RE_3,       MI_3, MI_3, FA_3, SOL_3, SOL_3, FA_3, MI_3, RE_3,    DO_3, DO_3, RE_3, MI_3, RE_3,    PAUSA,   DO_3, DO_3,     RE_3,    PAUSA,     MI_3, DO_3, RE_3, MI_3, FA_3, MI_3, DO_3,     RE_3, MI_3, FA_3, MI_3, RE_3, DO_3, RE_3, SI_3   
                      };
//DURA��O NOTAS DAS MUSICAS, CADA LINHA REPRESENTA UMA MUSICA                      
   int8 duracao[200] ={8,8,8,4,8,4,8,8,8,4,8,4,8,8,8,4,8,4,8,8,8,4,  4,   8,8,8,4,8,4,8,8,8,4,8,4,8,8,8,4,8,4,8,8,8,4,       
                       8,8,   4,4,4,4,    4,2,8,8,    4,4,4,4,    2,8,8,8,    4,4,  8,  8,8,8,       4,4,   8  ,8,8,8,      4,4,8,8,8,4     8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,
                       8,16,16,8,4,8,8,16,16,     2,4,8,16,16,    8,4,8,8,16,16,4,    4,16,8,8,16,16,8,4,   8,8,16,16,4,4,    8,16,16,4,8,16,16,4,    8,16,16,4,8,16,16,4,    8,
                       8,8,8,8,8,8,8,8     8,8,8,8,8,    16,    16,4       8,8,8,8,8,8,8,8     8,8,8,8,8,    16,    16,4      8,    8,     8,8,8,16,16,8,8,      8,16,16,8,8,8,8,4
                      };
   //COMO FOI UTILIZADO UM VETOR PARA TODAS NOTAS, ENTAO PARA SEPARAR AS MUSICAS FOI UTILIZADA ESSA VARIAVEL AUXLIAR
   int8 quant_Notas[4] = {45,52,52,47}; // Quantidade de notas que cada musica possui
   
   int16 index_inicio[QUANT_MUSICAS];
   int i = 0;
   index_inicio[0] = 0;
   int16 acum = 0;
   
   //CRIA INDEXADORES QUE MARCAM AS POSI�OES DE INICIO E FIM DAS MUSICAS NOS VETORES "NOTAS" E "DURACAO" 
   for(i = 1;i<QUANT_MUSICAS;i++){
      int8 j=i;
      while(j>0){
         acum += quant_Notas[j-1];
         j--;
      }
      index_inicio[i] = acum;
      acum = 0;
   }
   //FIM BLOCO
   i=0;
   
   int16 acertos = 0;
   int16 tempo_musica =  (60000/sound[index].tempo)*4;      //Converte o tempo da musica de BPM p/ ms
   
   
   //MOSTRA AS NOTAS NOS LEDS E COMPARA SE O USUARIO ACERTOU, ACUMULANDO OS ACERTOS
   while(i<quant_Notas[index]){
      int8 acertosNota = 0;
      notesToLeds(notas[index_inicio[index]+i]);
      int16 duracao_nota = (int16) (tempo_musica/duracao[index_inicio[index]+i]);
   
      for(int j = 0; j<20;j++){
      
         int8 nota_Press_User = input_b();
         if(j>4 && j<10){
            if(nota_Press_User == notas[index_inicio[index]+i]){   //SE ACERTOU
            acertosNota++;
            }
         }
         

         delay_ms((int16)(duracao_nota/20));
         if(j==10) {
            apagarLeds();
            qualificarPrecisao(i, acertosNota);
            lcd_gotoxy(1,2);
            printf(lcd_putc, "NotaPress: %u    ",nota_Press_User);
         }
      }
      acertos+=acertosNota;
      i++;
   }
   //
   
   //CALCULO DA NOTA FINAL
   float pontuacao = (acertos/5);
   lcd_gotoxy(1,1);
   printf(lcd_putc, "\fAcertos: %lu/%u",(int16) ceil(pontuacao),quant_Notas[index]);
   pontuacao /= quant_Notas[index];
   pontuacao *= 100;
   pontuacao = ceil(pontuacao);
   
   int8 pontuacao_Final = (int) pontuacao;
  
   lcd_gotoxy(1,2);
   printf(lcd_putc, "Perc.Total:  %u%%", pontuacao_Final);
   delay_ms(8000);
   return pontuacao_Final;
   //
  
}

void salvar(int8 pontuacao,int8 index){


   int8 address = 0x00;
   int8 aux = 0;
   int8 memoria[10] = {0,0,0,0,0,0,0,0,0,0};  //Vetor aux
   int8 memoria_index[10] = {0,0,0,0,0,0,0,0,0,0}; //Vetor aux
   int8 offset = 10;
   
   /* MAPA DE MEMORIA DA EEPROM
   0X00 - 0X09 --> PONTUA�OES   
   0X0A - 0X13 --> INDEXADORES CORRESPONDENTES AS PONTUA�OES  
   */
   
   for(address = 0x00; address<10;address++){
      aux = read_eeprom(address);
      if(pontuacao>=aux){
         lcd_gotoxy(1,1);
         printf(lcd_putc, "\fRECORD ATINGIDO!\n%u COLOCADO", address+1);
         delay_ms(3000);
         for(int i = address; i<10;i++){
            memoria[i] = read_eeprom(i);
            memoria_index[i] = read_eeprom(i+offset);
         }
         for(int j = address+1; j<10;j++){
     
            write_eeprom(j,memoria[j-1]);
            write_eeprom(j+offset,memoria_index[j-1]);
         }
         write_eeprom(address,pontuacao);
         write_eeprom(address+offset,index);
         break;
      }
   } 
}


int listaMusicas(){
   
   int index = 1;
   printf(lcd_putc, "\f");
   lcd_gotoxy(1,1);
   printf(lcd_putc, "    MUSICAS:");
  
   while(flag_control){
     
      // VERIFICA SE ALGUM DOS BOTOES FORAM PRESSIONADOS
      if(input(BTN_ENTER)) flag_ENTER = TRUE;
      if(input(BTN_UP)) flag_UP = TRUE;
      if(input(BTN_CLOSE)) flag_CLOSE = TRUE;
      if(input(BTN_DOWN)) flag_DOWN = TRUE;
      //
      
      //SE ALGUM BOTAO FOI PRESSIONADO DEPOIS SOLTO, UM DESSES IFs SER� ACIONADO
      if(!input(BTN_UP) && flag_UP) {
         flag_UP = FALSE;
         if(index == 1) index = QUANT_MUSICAS; else index--;
         lcd_gotoxy(1,2);
         printf(lcd_putc, "                ");
         
      }
      if(!input(BTN_DOWN) && flag_DOWN) {
         if(index == QUANT_MUSICAS) index = 1; else index++;
         lcd_gotoxy(1,2);
         printf(lcd_putc, "                ");
         flag_DOWN = FALSE;
      }
      lcd_gotoxy(1,2);
      printf(lcd_putc, "%u.%s", index, sound[index-1].titulo);
  
      if(!input(BTN_ENTER) && flag_ENTER){ 
        flag_ENTER = false;
        flag_control = false;
        
      }
      if(!input(BTN_CLOSE) && flag_CLOSE){ 
        flag_CLOSE = false;
        flag_control = false;
        index = 0;
      }
      //
   }
   
   return index;
}



void records(){

int index = 1;
printf(lcd_putc,"\f");
  
   while(flag_control){
     
      // VERIFICA SE ALGUM DOS BOTOES FORAM PRESSIONADOS
      if(input(BTN_UP)) flag_UP = TRUE;
      if(input(BTN_CLOSE)) flag_CLOSE = TRUE;
      if(input(BTN_DOWN)) flag_DOWN = TRUE;
      //
      //SE ALGUM BOTAO FOI PRESSIONADO DEPOIS SOLTO, UM DESSES IFs SER� ACIONADO
      if(!input(BTN_UP) && flag_UP) {
         flag_UP = FALSE;
         if(index == 1) index = 10; else index--;
         lcd_gotoxy(1,2);
         printf(lcd_putc, "                ");
         
      }
      if(!input(BTN_DOWN) && flag_DOWN) {
         if(index == 10) index = 1; else index++;
         lcd_gotoxy(1,2);
         printf(lcd_putc, "                ");
         flag_DOWN = FALSE;
      }
      
      //PRINTA NO LCD OS RECORDS
      int8 tituloMusicaAtual = read_eeprom(index-1+10);
      int8 pontMusicaAtual = read_eeprom(index-1);
      lcd_gotoxy(1,1);
      printf(lcd_putc, "%u.%s           ", index, sound[tituloMusicaAtual].titulo);
      lcd_gotoxy(1,2);
      printf(lcd_putc, "Pontuacao: %u%%", pontMusicaAtual);
      //
      if(!input(BTN_CLOSE) && flag_CLOSE){ 
        flag_CLOSE = false;
        flag_control = false;
      }
   
   }
   
}


int menuPrincipal(){
   
   
   char tituloMenu[2][8] = {"MUSICAS", "RECORDS"};
   int index = 1;
   printf(lcd_putc, "\f");
   lcd_gotoxy(1,1);
   printf(lcd_putc, " MENU PRINCIPAL ");
   
   while(flag_control){
      // VERIFICA SE ALGUM DOS BOTOES FORAM PRESSIONADOS
      if(input(BTN_UP)) flag_UP = TRUE;
      if(input(BTN_DOWN)) flag_DOWN = TRUE;
      if(input(BTN_ENTER)) flag_ENTER = TRUE;
      //
      //SE ALGUM BOTAO FOI PRESSIONADO DEPOIS SOLTO, UM DESSES IFs SER� ACIONADO
      if(!input(BTN_UP) && flag_UP) {
         flag_UP = FALSE;
         if(index == 1) index = 2; else index--;
         lcd_gotoxy(1,2);
         printf(lcd_putc, "                ");
         
      }
      if(!input(BTN_DOWN) && flag_DOWN) {
         if(index == 2) index = 1; else index++;
         lcd_gotoxy(1,2);
         printf(lcd_putc, "                ");
         flag_DOWN = FALSE;
      }
      
      //PRINTA AS O NOME DA MUSICA NO LCD
      lcd_gotoxy(1,2);
      printf(lcd_putc, "%u.%s", index, tituloMenu[index-1]);
      //
      if(!input(BTN_ENTER) && flag_ENTER){ 
         flag_ENTER = false;
         flag_control = false;
      }
   }
   
   if (index==1) return 1; else return 2;
}


void qualificarPrecisao(int8 index, int8 acertos_nota){ 
char texto[12];
   switch(acertos_nota){
      case 0:
      texto = "ERROU";
      break;
      case 1:
      texto = "PESSIMO";
      break;
      case 2:
      texto = "RUIM";
      break;
      case 3:
      texto = "REGULAR";
      break;
      case 4:
      texto = "BOM";
      break;
      case 5:
      texto = "PERFEITO!!";
      break;
   }
   lcd_gotoxy(1,1);
   printf(lcd_putc,"\f%u. %s", index, texto);
}

void animacaoLogo(){
   int x = 0;
   while(x<5){
      printf(lcd_putc,"\f");
      delay_ms(250);
      lcd_gotoxy(1,1);
      printf(lcd_putc, "HARP LASER MUSIC");
      delay_ms(350);
      x++;
   }
   
   lcd_gotoxy(1,2);
   printf(lcd_putc, "+-+PLAY GAME!+-+");
   delay_ms(2000);
}

void apagarLeds(){
   output_low(LED1);
   output_low(LED2);
   output_low(LED3);
   output_low(LED4);
   output_low(LED5);
   output_low(LED6);
   output_low(LED7);
   output_low(LED8);
}

void ledEffect(){
   int i = 0;
   while(i<10){
      
      notesToLEds(DO_3);
      delay_ms(50);
        notesToLEds(RE_3);
      delay_ms(50);
        notesToLEds(MI_3);
      delay_ms(50);
        notesToLEds(FA_3);
      delay_ms(50);
        notesToLEds(SOL_3);
      delay_ms(50);
        notesToLEds(LA_3);
      delay_ms(50);
        notesToLEds(SI_3);
      delay_ms(50);
        notesToLEds(DO_4);
      delay_ms(50);
      i++;
   }
}

void notesToLeds(int8 note){
   apagarLeds();
   switch(note){
      case 0b11111110:
      output_high(LED1);
      break;
      case 0b11111101:
      output_high(LED2);
      break;
      case 0b11111011:
      output_high(LED3);
      break;
      case 0b11110111:
      output_high(LED4);
      break;
      case 0b11101111:
      output_high(LED5);
      break;
      case 0b11011111:
      output_high(LED6);
      break;
      case 0b10111111:
      output_high(LED7);
      break;
      case 0b01111111:
      output_high(LED8);
      break;
   
   }
}

