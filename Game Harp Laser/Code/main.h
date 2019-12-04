#include <18F4550.h>
#device ADC=16

#FUSES NOWDT                    //No Watch Dog Timer
#FUSES WDT128                   //Watch Dog Timer uses 1:128 Postscale
#FUSES NOBROWNOUT               //No brownout reset
#FUSES NOLVP                    //No low voltage prgming, B3(PIC16) or B5(PIC18) used for I/O
#FUSES NOXINST                  //Extended set extension and Indexed Addressing mode disabled (Legacy mode)

#use delay(crystal=20000000)
#use FIXED_IO( A_outputs=PIN_A4,PIN_A3,PIN_A2,PIN_A1,PIN_A0 )
#use FIXED_IO( E_outputs=PIN_E1,PIN_E0 )

#define LED1   PIN_A0
#define LED2   PIN_A1
#define LED3   PIN_A2
#define LED4   PIN_A3
#define LED5   PIN_A4
#define LED6   PIN_C2
#define LDR_DO_3   PIN_B0
#define LDR_RE_3   PIN_B1
#define LDR_MI_3   PIN_B2
#define LDR_FA_3   PIN_B3
#define LDR_SOL_3   PIN_B4
#define LDR_LA_3   PIN_B5
#define LDR_SI_3   PIN_B6
#define LDR_DO_4   PIN_B7
#define BTN_UP   PIN_E2
#define BTN_DOWN   PIN_D0
#define BTN_ENTER   PIN_C6
#define BTN_CLOSE   PIN_C7
#define LED7   PIN_E0
#define LED8   PIN_E1


