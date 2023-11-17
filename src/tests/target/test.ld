ENTRY(_Reset)
SECTIONS
{
 # Start at 1 GiB, which is the start of ram on the qemu virtual board
 # https://www.qemu.org/docs/master/system/arm/virt.html
 . = 0x40000000; # for use with -M virt
 .start . : { start.o(.text) }
 .reset . : { reset.o(.text) }
 .print . : { print.o(.text) }
 .main . : { main.o(.text) }
 .text : { *(.text) }
 .data : { *(.data) }
 .bss : { *(.bss COMMON) }
 . = ALIGN(8);
 . = . + 0x1000; /* 4kB of stack memory */
 stack_top = .;
}
