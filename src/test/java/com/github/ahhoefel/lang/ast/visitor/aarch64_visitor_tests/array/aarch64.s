.global main
// Function: main
main: // Function definition
SUB sp, sp, #312
STR w30, [sp, #0]
// var a Array[int] = new Array[int](3)
// New Expression
MOV x0, #0x3
STR x0, [sp, #32]
LDR x0, [sp, #32]
STR x1, [sp, #56]
MOV x1, #0x8 // Array element width bytes
STR x1, [sp, #64]
MUL x0, x0, x1
STR x0, [sp, #48]
LDR x1, [sp, #48]
LDR x0, heap_bottom
ADD x1, x0, x1
STR x1, [sp, #40]
LDR x1, =heap_bottom
STR x0, [x1, #0]
LDR x0, [sp, #40]
STR x0, [sp, #72]
LDR x0, [sp, #64]
STR x0, [sp, #88]
LDR x0, [sp, #56]
STR x0, [sp, #80]
// a[0] = 1
MOV x0, #0x1
STR x0, [sp, #96]
// LValue
MOV x0, #0x0
STR x0, [sp, #104]
LDR x0, [sp, #88]
LDR x1, [sp, #104]
MUL x0, x0, x1
LDR x1, [sp, #72]
ADD x0, x0, x1
STR x0, [sp, #120]
LDR x0, [sp, #96]
LDR x1, [sp, #120]
STR x0, [x1, #0] // Indirect assign
// a[1] = 2
MOV x0, #0x2
STR x0, [sp, #136]
// LValue
MOV x0, #0x1
STR x0, [sp, #144]
LDR x0, [sp, #88]
LDR x1, [sp, #144]
MUL x0, x0, x1
LDR x1, [sp, #72]
ADD x0, x0, x1
STR x0, [sp, #160]
LDR x0, [sp, #136]
LDR x1, [sp, #160]
STR x0, [x1, #0] // Indirect assign
// a[2] = 4
MOV x0, #0x4
STR x0, [sp, #176]
// LValue
MOV x0, #0x2
STR x0, [sp, #184]
LDR x0, [sp, #88]
LDR x1, [sp, #184]
MUL x0, x0, x1
LDR x1, [sp, #72]
ADD x0, x0, x1
STR x0, [sp, #200]
LDR x0, [sp, #176]
LDR x1, [sp, #200]
STR x0, [x1, #0] // Indirect assign
// var i int = 0
MOV x0, #0x0
STR x0, [sp, #216]
LDR x0, [sp, #216]
STR x0, [sp, #224]
// var n int = 0
MOV x0, #0x0
STR x0, [sp, #232]
LDR x0, [sp, #232]
STR x0, [sp, #240]
before_for_0: // For loop
STR x0, [sp, #248]
LDR x0, [sp, #224]
LDR x1, [sp, #248]
CMP x0, x1
CSET x0, LT
STR x0, [sp, #256]
LDR x0, [sp, #256]
CMP x0, #0
B.EQ after_for_0
// n = n + a[i]
LDR x0, [sp, #88]
LDR x1, [sp, #224]
MUL x0, x0, x1
LDR x1, [sp, #72]
ADD x0, x0, x1
LDR x0, [x0, #0] // Dereference
STR x0, [sp, #272]
LDR x0, [sp, #240]
LDR x1, [sp, #272]
ADD x0, x0, x1
STR x0, [sp, #288]
// LValue
LDR x0, [sp, #288]
STR x0, [sp, #240] // Direct assign
// i = i + 1
MOV x0, #0x1
STR x0, [sp, #296]
LDR x0, [sp, #224]
LDR x1, [sp, #296]
ADD x0, x0, x1
STR x0, [sp, #304]
// LValue
LDR x0, [sp, #304]
STR x0, [sp, #224] // Direct assign
B before_for_0
after_for_0:
LDR x0, [sp, #240]
B fn_return_3
fn_return_3:
LDR w30, [sp, #0]
ADD sp, sp, #312
RET 
