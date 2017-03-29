/*!
 * file: StringList_test.cpp
 * StringList class test
 * written: 15/03/2017
 */

#include "StringList.h"

int main(){

	StringList sl;
	sl.addHead("Igor");
	printf("%d %d\n",sl.getSize(), sl.isEmpty());

	return 0;	
} 

