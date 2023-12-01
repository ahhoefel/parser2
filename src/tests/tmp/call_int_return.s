.global foo
.global main
foo:
SUB sp, sp, #16
STR w30, [sp, #0]
MOV x0, #0x3
STR x0, [sp, #8]
LDR x0, [sp, #8]
B fn_return_1
fn_return_1:
LDR w30, [sp, #0]
ADD sp, sp, #16
RET 
main:
SUB sp, sp, #32
STR w30, [sp, #0]
BL foo
STR x0, [sp, #8]
MOV x0, #0x1
STR x0, [sp, #16]
LDR x0, [sp, #8]
LDR x1, [sp, #16]
ADD x0, x0, x1
STR x0, [sp, #24]
LDR x0, [sp, #24]
B fn_return_0
fn_return_0:
LDR w30, [sp, #0]
ADD sp, sp, #32
RET 
