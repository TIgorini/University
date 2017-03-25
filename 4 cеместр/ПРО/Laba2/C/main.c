#include "types.h"
#include "input.h"
#include "output.h"
#include "search.h"

int main(){

	mass matrix;
	in(&matrix);
	out(matrix);
	search(matrix);	
		
	return 0;
}
