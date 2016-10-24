/************************************************************************
*file: argz.c
*synopsis: These functions are declared in the include file "argz.h".
*author: Igor Tymoshenko
*written: 22/10/2016
*last modified: 23/10/2016
************************************************************************/

#include "argz.h"

/*
The argz_create_sep function converts the null-terminated string string into an
argz vector (returned in argz and argz len) by splitting it into elements at every
occurrence of the character sep.
*/
error_t argz_create_sep (const char *string, int sep, char **argz, size_t *argz_len){

	int i;
	*argz_len = strlen(string) + 1;
	*argz = (char*)malloc( *argz_len*sizeof(char));
	if ((sep > 255 || sep < 0) && ( *argz == NULL))
		return ENOMEM;

	for (i = 0; i < *argz_len; i++)
		if ( *(string + i) == sep)
			*(*argz + i) = '\0';
		else
			*(*argz + i) = *(string + i);

	return OK;
}

/*
Returns the number of elements in the argz vector.
*/
size_t argz_count (const char *argz, size_t arg_len){

	int i;
	size_t count = 0; 
	for ( i = 0; i < arg_len; i++)
		if ( *(argz + i) == '\0')
			count++;

	return count;
}

/*
The argz_add function adds the string str to the end of the argz vector // *argz, and 
updates *argz and *argz_len accordingly.
*/
error_t argz_add (char **argz, size_t *argz_len, const char *str){
	
	int i;
	int length = strlen(str) + 1;
	*argz = (char*)realloc(*argz, (*argz_len + length)*sizeof(char));
	if ( *argz == NULL)
		return ENOMEM;

	for (i = 0; i <= length; i++)
		*(*argz + *argz_len + i) = *(str + i);

	*argz_len += length;	

	return OK;
}


/*
If entry points to the beginning of one of the elements in the argz vector *argz, 
the argz_delete function will remove this entry and reallocate *argz, modifying *argz 
and *argz_len accordingly. 
*/
void argz_delete (char **argz, size_t *argz_len, char *entry){
	
	size_t del_len;
	if ((entry == *argz) || (*(entry - 1) == '\0')){

		del_len = strlen(entry) + 1;
		memmove(entry, entry + del_len, *argz_len - (entry - *argz + del_len));
		*argz_len -= del_len;
		*argz = (char*)realloc(*argz, *argz_len); 
	}
}

/*
The argz_insert function inserts the string entry into the argz vector *argz at a point 
just before the existing element pointed to by before, reallocating *argz and updating *argz 
and *argz_len.
*/
error_t argz_insert (char **argz, size_t *argz_len, char *before, const char *entry){

	int length = strlen(entry) + 1;
	int i;

	if ( before == NULL ){

		if ( argz_add(argz, argz_len, entry) == ENOMEM )
			return ENOMEM;
	}
	else{

		*argz = (char*)realloc(*argz, (*argz_len + length)*sizeof(char));
		if ( *argz == NULL )
			return ENOMEM;
		
		memmove(before + length, before, (*argz_len - (before - *argz))*sizeof(char));
		for (i = 0; i < length; i++)
			*(before + i) = *(entry + i);
		*argz_len += length;
	}

	return OK;
}

/*
The argz_next function provides a convenient way of iterating over the elements in the argz 
vector argz. It returns a pointer to the next element in argz after the element entry, or 0 if 
there are no elements following entry. If entry is 0, the first element of argz is returned.
*/
char * argz_next (char *argz, size_t argz_len, const char *entry){

	int i = 0;
	if (entry == NULL)
		return argz;

	while ( i < argz_len){

		if (entry == argz + i){

			i += strlen(argz + i) + 1;
			if ( i < argz_len)
				return argz + i;
		}
		i += strlen(argz + i) + 1;
	}
	
	return NULL;
}

/*
Replace the string str in argz with string with, reallocating argz as
necessary. 
*/
error_t argz_replace (char **argz, size_t *argz_len, const char *str, const char *with){
	
	int len1 = strlen(str);
	int len2 = strlen(with);
	int d_len = len2 - len1;	//delta length
	char *entry = NULL;
	char *begin = NULL;
	int i;

	if (len1 == 0 )
		return ENOMEM;

	while (entry = argz_next(*argz, argz_len, entry)){

		begin = strstr(entry, str);
		if ( !begin ){

			if ( d_len < 0){

				memmove(begin + len2, begin + len1, (*argz_len - (begin - *argz + len1))*sizeof(char));
				*argz = (char*)realloc(*argz, *argz_len + d_len);
			} else{

				*argz = (char*)realloc(*argz, *argz_len + d_len);
				memmove(begin + len2, begin + len1, (*argz_len - (begin - *argz + len1))*sizeof(char));
			}
			for (i = 0; i < len2; i++)
				*(begin + i) = *(with + i);

			return OK;
		}
	}
	
	return ENOMEM;
}

/*
prints argz vector 
*/
void argz_print(char *argz, size_t argz_len){

	char *entry = NULL;
	while ((entry = argz_next(argz, argz_len, entry)))
		printf("%s\n", entry);
	
}