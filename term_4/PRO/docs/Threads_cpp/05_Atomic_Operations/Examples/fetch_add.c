#include <stdio.h>
int main()
{
        long long b = 2;
        printf("%lld\n", __sync_fetch_and_add_8 (&b, 3));
        printf("%lld\n", b);
        return 0;
}
