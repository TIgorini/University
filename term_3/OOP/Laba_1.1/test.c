#include "mystring.c"

int main(){

	char *str1 = "HelsoleH";
	char *str2 = "so";
	char *doubleStr = "1;2;3.5;-8c;4a4";
	double *doubleArr;
	int size = 0, i = 0; 

	printf("1. \"%s\" <-- \"%s\" in %d position \n", str1, str2, substr(str1, str2));
	printf("2. %d\n",subseq(str1, str2));
	printf("3. %d\n",ispal(str1));
	printf("4. \"%s\" --> \"%s\"\n", str2, makepal(str2));

	doubleArr = txt2double(doubleStr, &size);
	printf("5. String:  %s\n", doubleStr);
	if ( size )
		for ( i; i < size; i++ )
			printf("     %f\n",doubleArr[i]);
	else  
		printf("   Please, try again\n");

	free(doubleArr);

	return 0;
}