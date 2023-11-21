volatile unsigned int * const UART0DR = (unsigned int *)0x09000000;

void print_uart0(unsigned int s) {
        unsigned int a[20];
	int i = 0;
	do {
		a[i] = 48 + (s%10);
		s = s/10;
                i++;
        } while (s > 0);
	for (int j = i-1; j >= 0; j--) {
        	*UART0DR = a[j]; /* Transmit char */
	}
       	*UART0DR = 10; // Newline
}

void c_entry() {
	print_uart0(123456);
}

