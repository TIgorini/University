#include "search.h"

void search(mass ar){

	int i, j,
        max_posi = 0,
        max_posj = 0;
    int max = ar[0][0];
    for (i = 0; i < m; i++) {
        for (j = 0; j < n; j++) {

            if (max <= ar[i][j]){
                max = ar[i][j];
                max_posi = i;
                max_posj = j;
            }
        }
    }
    printf("Last max: %d\n", max);
    printf("Position: %d, %d\n", max_posi, max_posj);
}