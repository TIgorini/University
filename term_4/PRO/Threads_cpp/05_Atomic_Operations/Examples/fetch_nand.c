#include <stdio.h>
int main()
{
        int b = 4;
        printf("%d\n", __sync_fetch_and_nand (&b, 7));
        printf("%d\n", b);
        return 0;
}
