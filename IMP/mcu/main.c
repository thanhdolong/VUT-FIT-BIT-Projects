#include <fitkitlib.h>
#include <stddef.h>

#include <irq/fpga_interrupt.h>
#include <keyboard/keyboard.h>

#define DIT_LEN 0x0600
#define SYMBOL_TIMEOUT 4 * DIT_LEN
#define SLEEP_TIMEOUT 3 * SYMBOL_TIMEOUT

#define _7_INVALID 0xBF
#define _7_BLANK 0xFF

typedef struct leaf {
  char symbol;
  struct leaf* dit;
  struct leaf* dash;
  char _7segmentCode;
} leaf_t;

leaf_t _0 = {'0', NULL, NULL, 0xC0};
leaf_t _1 = {'1', NULL, NULL, 0xF9};
leaf_t _2 = {'2', NULL, NULL, 0xA4};
leaf_t _3 = {'3', NULL, NULL, 0xB0};
leaf_t _4 = {'4', NULL, NULL, 0x99};
leaf_t _5 = {'5', NULL, NULL, 0x92};
leaf_t _6 = {'6', NULL, NULL, 0x82};
leaf_t _7 = {'7', NULL, NULL, 0xF8};
leaf_t _8 = {'8', NULL, NULL, 0x80};
leaf_t _9 = {'9', NULL, NULL, 0x98};

leaf_t _H = {'H', &_5, &_4, 0x89 - 0x80};
leaf_t _V = {'V', NULL, &_3, _7_INVALID};
leaf_t _F = {'F', NULL, NULL, 0x8E - 0x80};
leaf_t u1 = {0, NULL, &_2, _7_INVALID};
leaf_t _L = {'L', NULL, NULL, 0xC7 - 0x80};
leaf_t _P = {'P', NULL, NULL, 0x8C - 0x80};
leaf_t _J = {'J', NULL, &_1, 0xE1 - 0x80};
leaf_t _B = {'B', &_6, NULL, 0x80 - 0x80};
leaf_t _X = {'X', NULL, NULL, _7_INVALID};
leaf_t _C = {'C', NULL, NULL, 0xC6 - 0x80};
leaf_t _Y = {'Y', NULL, NULL, _7_INVALID};
leaf_t _Z = {'Z', &_7, NULL, _7_INVALID};
leaf_t _Q = {'Q', NULL, NULL, _7_INVALID};
leaf_t u3 = {0, &_8, NULL, _7_INVALID};
leaf_t u4 = {0, &_9, &_0, _7_INVALID};

leaf_t _S = {'S', &_H, &_V, 0x92 - 0x80};
leaf_t _U = {'U', &_F, &u1, 0xC1 - 0x80};
leaf_t _R = {'R', &_L, NULL, _7_INVALID};
leaf_t _W = {'W', &_P, &_J, _7_INVALID};
leaf_t _D = {'D', &_B, &_X, 0xC0 - 0x80};
leaf_t _K = {'K', &_C, &_Y, _7_INVALID};
leaf_t _G = {'G', &_Z, &_Q, 0x82 - 0x80};
leaf_t _O = {'O', &u3, &u4, 0xC0 - 0x80};

leaf_t _I = {'I', &_S, &_U, 0xF9 - 0x80};
leaf_t _A = {'A', &_R, &_W, 0x88 - 0x80};
leaf_t _N = {'N', &_D, &_K, _7_INVALID};
leaf_t _M = {'M', &_G, &_O, _7_INVALID};

leaf_t _E = {'E', &_I, &_A, 0x86 - 0x80};
leaf_t _T = {'T', &_N, &_M, _7_INVALID};

leaf_t root = {0, &_E, &_T, _7_BLANK};

leaf_t* state = &root;


void fpga_initialized() {
  term_send_str_crlf("Dekoder Morseovy abecedy na 7-segmentovce");
  fpga_interrupt_init(BIT2);
}

char is_pressed = 0;
void fpga_interrupt_handler(unsigned char bits) {
  if (bits & BIT2) { 
    if (is_pressed == 0 && key_decode(read_word_keyboard_4x4())) {
      is_pressed = 1;
      TACCTL0 &= ~CCIE;
      TAR = 0;
      term_send_str("Interupt pressed\n");
    } else if (is_pressed && key_decode(read_word_keyboard_4x4()) == 0) {
      is_pressed = 0;
      if (TAR < DIT_LEN) {
        if (state->dit)
          state = state->dit;
      } else {
        if (state->dash)
          state = state->dash;
      }

      TAR = 0;
      TACCR0 = SYMBOL_TIMEOUT;
      TACCTL0 = CCIE;

      term_send_char(state->symbol);
      term_send_str_crlf(" is in state");
      term_send_str("Interupt released\n");
    }
  }
}

char older[3] = {_7_BLANK, _7_BLANK, _7_BLANK};
interrupt (TIMERA0_VECTOR) Timer_A (void) {
  TACCTL0 &= ~CCIE;
  if (TACCR0 == SYMBOL_TIMEOUT) {
    if (state->_7segmentCode != (char) _7_INVALID) {
      older[0] = older[1];
      older[1] = older[2];
      older[2] = state->_7segmentCode;
    }

    state = &root;
    
    TAR = 0;
    TACCR0 = SLEEP_TIMEOUT;
    TACCTL0 = CCIE;

    term_send_str_crlf("SYMBOL_TIMEOUT");
  }
  else {

      older[0] = older[1] = older[2] = _7_BLANK;
    state = &root;
    term_send_str_crlf("SLEEP_TIMEOUT");
  }
}

/*******************************************************************************
 * Hlavni funkce
*******************************************************************************/
int main(void) { 
  initialize_hardware();

  P4DIR |= 0x0F;
  P6DIR |= 0xFF;

  WDTCTL = WDTPW + WDTHOLD;
  TACTL = TASSEL_1 + ID_3 + MC_2;
  
  while (1) {
    P4OUT = 0x01;
    P6OUT = older[0];
    delay_ms(2);
    P4OUT = 0x02;
    P6OUT = older[1];
    delay_ms(2);
    P4OUT = 0x04;
    P6OUT = older[2];
    delay_ms(2);
    P4OUT = 0x08;
    P6OUT = state->_7segmentCode;
    delay_ms(2);
    
    terminal_idle();
  }
}

void print_user_help(void) {
  ;
}

unsigned char decode_user_cmd(char *cmd_ucase, char *cmd) {
  return CMD_UNKNOWN;
}
