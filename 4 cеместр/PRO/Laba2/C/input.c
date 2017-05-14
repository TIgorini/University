#include "input.h"

void in(mass* ar){

	int i,j;
	srand((unsigned)time(NULL));
	for (i = 0; i < m; i++)
		for (j = 0; j < m; j++)
			*ar[i][j] = rand() % 50;
}
