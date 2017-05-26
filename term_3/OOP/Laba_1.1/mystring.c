#include "mystring.h"

int substr(const char *string1, const char *string2)
{
	char *res = strstr( string1, string2 );
	return ( res ? ( res - string1 + 1 ) : 0 ); 
}

/* numbering starts at 1; 0 means that str2 is not included in str1 */
int subseq(const char *string1, const char *string2)
{
	int maxlen = 0;
	int len = 0;
	int i;

	while( *string1 )
	{
		i = 0;
		while ( *(string2 + i))
		{
			while (( *(string1 + i) == *(string2 + i) ) && ( *(string1 + i)))
			{ 	
				len ++; 
				i++;
			}
			if ( maxlen < len )	
				maxlen = len;	
			len = 0;
			i++;
		}	
		string1++;
	}

	return maxlen;
}

char ispal(const char *string)
{
	const char *strbeg = string;
	char bufstr[strlen(string) + 1];
	int i = 1;

	strcpy(bufstr, string);

	while ( bufstr[strlen(bufstr) - i] = *string++ ) 
		i++; 

	return ( strstr(bufstr, strbeg) ? 1 : 0 );
}

char* makepal(const char *string)
{
	int i = 0;
	char *bufstr = (char *)malloc(2*strlen(string) + 1);
	strcpy(bufstr, string);

	while ( !ispal(string + i) )
		i++;

	for ( i -= 1; i >= 0; i-- )
		strncat(bufstr, string + i, 1);
		
	return bufstr;
}

double* txt2double(const char *string, int *size)
{
	double *resArr = (double*)malloc(40);
	int i = 0;

	while ( *string )
	{
		resArr[i] = atof(string);
		if (resArr[i] == 0)
		{
			*size = 0;
			return resArr;
		}

		while (( *string != ';') && ( *string))
			string++;
		i++;
		string++;
	} 

	*size = i;
	return resArr;
}
