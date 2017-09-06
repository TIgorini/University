#include <stdio.h>
int main()
{
        int b = 2;
        printf("%d\n", __sync_or_and_fetch (&b, 5));
        printf("%d\n", b);
        return 0;
}
