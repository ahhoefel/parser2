.global _Reset
_Reset:
  LDR x0, =stack_top
  MOV sp, x0
  BL main
  BL print_uart0 

