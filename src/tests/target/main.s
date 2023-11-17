.global other
.global main
other:
SUB sp, sp, #248
MOV x0, #0x1
STR x0, [sp, #0]
MOV x0, #0x1
STR x0, [sp, #8]
LDR x0, [sp, #0]
LDR x1, [sp, #8]
ADD x0, x0, x1
STR x0, [sp, #16]
MOV x0, #0x2
STR x0, [sp, #24]
LDR x0, [sp, #16]
LDR x1, [sp, #24]
CMP x0, x1
CSET x0, NE
STR x0, [sp, #32]
LDR x0, [sp, #32]
CMP x0, #0
B.EQ after_if_0
MOV x0, #0x0
STR x0, [sp, #40]
LDR x0, [sp, #40]
B fn_return_1
after_if_0:
MOV x0, #0x1
STR x0, [sp, #48]
MOV x0, #0x1
STR x0, [sp, #56]
LDR x0, [sp, #48]
LDR x1, [sp, #56]
ADD x0, x0, x1
STR x0, [sp, #64]
MOV x0, #0x3
STR x0, [sp, #72]
LDR x0, [sp, #64]
LDR x1, [sp, #72]
CMP x0, x1
CSET x0, EQ
STR x0, [sp, #80]
LDR x0, [sp, #80]
CMP x0, #0
B.EQ after_if_1
MOV x0, #0x0
STR x0, [sp, #88]
LDR x0, [sp, #88]
B fn_return_1
after_if_1:
MOV x0, #0x1
STR x0, [sp, #96]
MOV x0, #0x1
STR x0, [sp, #104]
LDR x0, [sp, #96]
LDR x1, [sp, #104]
SUB x0, x0, x1
STR x0, [sp, #112]
MOV x0, #0x0
STR x0, [sp, #120]
LDR x0, [sp, #112]
LDR x1, [sp, #120]
CMP x0, x1
CSET x0, NE
STR x0, [sp, #128]
LDR x0, [sp, #128]
CMP x0, #0
B.EQ after_if_2
MOV x0, #0x0
STR x0, [sp, #136]
LDR x0, [sp, #136]
B fn_return_1
after_if_2:
MOV x0, #0x2
STR x0, [sp, #144]
MOV x0, #0x3
STR x0, [sp, #152]
LDR x0, [sp, #144]
LDR x1, [sp, #152]
MUL x0, x0, x1
STR x0, [sp, #160]
MOV x0, #0x6
STR x0, [sp, #168]
LDR x0, [sp, #160]
LDR x1, [sp, #168]
CMP x0, x1
CSET x0, NE
STR x0, [sp, #176]
LDR x0, [sp, #176]
CMP x0, #0
B.EQ after_if_3
MOV x0, #0x0
STR x0, [sp, #184]
LDR x0, [sp, #184]
B fn_return_1
after_if_3:
MOV x0, #0x4
STR x0, [sp, #192]
MOV x0, #0x3
STR x0, [sp, #200]
LDR x0, [sp, #192]
LDR x1, [sp, #200]
SUB x0, x0, x1
STR x0, [sp, #208]
MOV x0, #0x1
STR x0, [sp, #216]
LDR x0, [sp, #208]
LDR x1, [sp, #216]
CMP x0, x1
CSET x0, NE
STR x0, [sp, #224]
LDR x0, [sp, #224]
CMP x0, #0
B.EQ after_if_4
MOV x0, #0x0
STR x0, [sp, #232]
LDR x0, [sp, #232]
B fn_return_1
after_if_4:
MOV x0, #0x1
STR x0, [sp, #240]
LDR x0, [sp, #240]
B fn_return_1
fn_return_1:
ADD sp, sp, #248
RET 
main:
SUB sp, sp, #56
MOV x0, #0x1
STR x0, [sp, #0]
MOV x0, #0x1
STR x0, [sp, #8]
LDR x0, [sp, #0]
LDR x1, [sp, #8]
ADD x0, x0, x1
STR x0, [sp, #16]
MOV x0, #0x2
STR x0, [sp, #24]
LDR x0, [sp, #16]
LDR x1, [sp, #24]
CMP x0, x1
CSET x0, NE
STR x0, [sp, #32]
LDR x0, [sp, #32]
CMP x0, #0
B.EQ after_if_5
MOV x0, #0x0
STR x0, [sp, #40]
LDR x0, [sp, #40]
B fn_return_0
after_if_5:
MOV x0, #0x1
STR x0, [sp, #48]
LDR x0, [sp, #48]
B fn_return_0
fn_return_0:
ADD sp, sp, #56
RET 
