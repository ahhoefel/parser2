.global main
main:
SUB sp, sp, #56
STR w30, [sp, #0]
MOV x0, #0xc
STR x0, [sp, #8]
LDR x0, [sp, #8]
STR x0, [sp, #16]
MOV x0, #0xd
STR x0, [sp, #24]
LDR x0, [sp, #24]
STR x0, [sp, #32]
LDR x0, [sp, #16]
LDR x1, [sp, #32]
ADD x0, x0, x1
STR x0, [sp, #40]
LDR x0, [sp, #40]
STR x0, [sp, #48]
fn_return_5:
LDR w30, [sp, #0]
ADD sp, sp, #56
RET 
