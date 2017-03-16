#include <stdio.h>
int main()
{
	int b = 3;
	printf("%d\n", __sync_val_compare_and_swap (&b, 2, 1));
	printf("%d\n", b);
	return 0;
}
