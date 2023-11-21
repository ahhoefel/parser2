.global main
.global boolReturn
main:
SUB sp, sp, #48
STR w30, [sp, #0]
BL boolReturn
STR x0, [sp, #8]
MOV x0, #0x1
STR x0, [sp, #16]
LDR x0, [sp, #8]
LDR x1, [sp, #16]
CMP x0, x1
CSET x0, NE
STR x0, [sp, #24]
LDR x0, [sp, #24]
CMP x0, #0
B.EQ after_if_0
MOV x0, #0x0
STR x0, [sp, #32]
LDR x0, [sp, #32]
B fn_return_0
after_if_0:
MOV x0, #0x1
STR x0, [sp, #40]
LDR x0, [sp, #40]
B fn_return_0
fn_return_0:
LDR w30, [sp, #0]
ADD sp, sp, #48
RET 
boolReturn:
SUB sp, sp, #16
STR w30, [sp, #0]
MOV x0, #0x1
STR x0, [sp, #8]
LDR x0, [sp, #8]
B fn_return_1
fn_return_1:
LDR w30, [sp, #0]
ADD sp, sp, #16
RET 
