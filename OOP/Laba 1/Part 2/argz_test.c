/************************************************************************
*file: argz_test.c
*purpose: testing operability of functions from "argz.c" 
*author: Igor Tymoshenko
*written: 22/10/2016
*last modified: 23/10/2016
*************************************************************************/

#include "argz.h" 

int main(){

	char *string = "I=dev/sda1;am=dev/sda2;Bat=dev/sda3";
	char *argz;
	size_t argz_len;

	printf("%s\n\n", string);
	// 1
	if ( argz_create_sep(string, ';', &argz, &argz_len) == OK){
		
		argz_print(argz, argz_len);
		// 2
		printf("\n%d\n\n", (int)argz_count(argz, argz_len));
		// 3 
		if ( argz_add(&argz, &argz_len, "man=dev/sda4") == OK )
			argz_print(argz, argz_len);
		// 4
		printf("\n");
		argz_delete(&argz, &argz_len, argz + 23);
		argz_print(argz, argz_len);
		//5
		printf("\n");
		if ( argz_insert(&argz, &argz_len, argz + 23, "Spider=dev/sda3") == OK)
			argz_print(argz, argz_len);
		//6
		printf("\n");
		if ( argz_replace(&argz, &argz_len, "der", "user") == OK){
			argz_print(argz, argz_len);
		}
				
	}

	free(argz);
}