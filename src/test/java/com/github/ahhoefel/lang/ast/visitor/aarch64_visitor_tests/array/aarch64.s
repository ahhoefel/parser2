.global main
main:
SUB sp, sp, #200
STR w30, [sp, #0]
LDR x0, [sp, #32]
STR x0, [sp, #40]
MOV x0, #0x1
STR x0, [sp, #48]
MOV x0, #0x0
STR x0, [sp, #56]
LDR x0, [sp, #48]
STR x0, [sp, #64]
MOV x0, #0x2
STR x0, [sp, #72]
MOV x0, #0x1
STR x0, [sp, #80]
LDR x0, [sp, #72]
STR x0, [sp, #88]
MOV x0, #0x4
STR x0, [sp, #96]
MOV x0, #0x2
STR x0, [sp, #104]
LDR x0, [sp, #96]
STR x0, [sp, #112]
MOV x0, #0x0
STR x0, [sp, #120]
LDR x0, [sp, #120]
STR x0, [sp, #128]
MOV x0, #0x0
STR x0, [sp, #136]
LDR x0, [sp, #136]
STR x0, [sp, #144]
before_for_0:
STR x0, [sp, #152]
LDR x0, [sp, #128]
LDR x1, [sp, #152]
CMP x0, x1
CSET x0, LT
STR x0, [sp, #160]
LDR x0, [sp, #160]
CMP x0, #0
B.EQ after_for_0
LDR x0, [sp, #144]
LDR x1, [sp, #168]
ADD x0, x0, x1
STR x0, [sp, #176]
LDR x0, [sp, #176]
STR x0, [sp, #144]
MOV x0, #0x1
STR x0, [sp, #184]
LDR x0, [sp, #128]
LDR x1, [sp, #184]
ADD x0, x0, x1
STR x0, [sp, #192]
LDR x0, [sp, #192]
STR x0, [sp, #128]
B before_for_0
after_for_0:
LDR x0, [sp, #144]
B fn_return_3
fn_return_3:
LDR w30, [sp, #0]
ADD sp, sp, #200
RET 
