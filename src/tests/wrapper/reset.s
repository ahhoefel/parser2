.global _Reset
_Reset:
  LDR x0, =stack_top
  MOV sp, x0
  LDR x0, =heap_bottom
  ADD x1, x0, 0x4
  STR x1, [x0, 0]
  BL main
  BL print_uart0 

