aarch64-none-elf-as -c aarch64.s -o aarch64.o
aarch64-none-elf-ld aarch64.o                
aarch64-none-elf-ld aarch64.o -o aarch64.elf
objdump -d aarch64.elf 
