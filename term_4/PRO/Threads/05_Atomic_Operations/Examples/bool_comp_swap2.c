#include <stdio.h>
int main()
{
	int b = 2;
	printf("%d\n", __sync_bool_compare_and_swap (&b, 2, 1));
	printf("%d\n", b);
	return 0;
}
