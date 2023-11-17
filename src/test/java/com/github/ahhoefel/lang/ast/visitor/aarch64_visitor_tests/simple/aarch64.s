main:
SUB sp, sp, #48
MOV x0, #0xc
STR x0, [sp, #0]
LDR x0, [sp, #0]
STR x0, [sp, #8]
MOV x0, #0xd
STR x0, [sp, #16]
LDR x0, [sp, #16]
STR x0, [sp, #24]
LDR x0, [sp, #8]
LDR x1, [sp, #24]
ADD x0, x0, x1
STR x0, [sp, #32]
LDR x0, [sp, #32]
STR x0, [sp, #40]
fn_return_5:
ADD sp, sp, #48
RET 
