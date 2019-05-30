## [INC] PIN verification

This program is implemented for FITkit device ([info](http://merlin.fit.vutbr.cz/FITkit/)).

Program is loaded to FITkit using QDevKit software. After start, verification using password is requested.
There are two valid passwords:
* `1626758238`
* `1607509153`

### Implementation
Program is implemented as Final State Machine (FSM). FSM scheme is available [here](/inc/inc-proj1/zprava.pdf).

For more info check [zadani.pdf](/inc/inc-proj1/zadani.pdf).

### Files
- `fsm.vhd` - source code of FSM
- `accerm.bin` - FPGA configuration (natively in build folder)
- `zprava.pdf` - scheme for FSM
- `zadani.pdf` - project assignment