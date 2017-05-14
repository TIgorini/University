#include "output.h"

void out(mass ar){

	int i,j;
	for (i = 0; i < m; i++){
		for (j = 0; j < n; j++)
			printf("%d ",ar[i][j]);
		printf("\n");
	}
}
