/*!
 * file: StringList_test.cpp
 * StringList class test
 * written: 15/03/2017
 */

#include "StringList.h"

int main(){

	StringList* sl = new StringList;
	sl->addHead("Igor");
	sl->addTail("Tymoshenko");
	sl->addHead("am");
	sl->addHead("I");
	sl->addTail("=)");
	printf("adding in head/tail:\n");	sl->printList(); 
	 
	StringList* sl2 = new StringList;
	sl2->addHead("Net");  sl2->addHead("Inter");
	sl->addHead(sl2); 
	sl->addTail(sl2);
	printf("adding list as element:\n");  sl->printList(); 
	
	sl->removeHead();
	sl->removeHead();
	sl->removeTail();
	printf("remove head/tail:\n");	sl->printList();

	sl2->addHead("Igor");
	sl2->addTail("=(");
	sl->appendExclusively(sl2);
	printf("append with: "); sl2->printList(); sl->printList();
	
	printf("splice with: "); 	sl2->printList(); 	
	sl->splice(sl->find("Net"), sl2, sl2->find("Igor"), sl2->find("Net"));
	sl->printList(); printf("list after splicing: "); sl2->printList();

	printf("unique:\n");
	sl->unique(); sl->printList();

	int indx = 2;
	printf("Element %d is '%s'\n\n", indx ,sl->getAt(indx));
	indx = 3;
	sl->removeAt(indx);   	 printf("remove at %d:\n", indx);  sl->printList();
	indx = 3;
	sl->setAt("Igor",indx);  printf("set at %d:\n", indx);     sl->printList();

	indx = 1;
	sl->insertAfter("super", indx);  printf("insert after %d:\n", indx);  sl->printList();
	indx = 4;
	sl->insertBefore("ninja", indx); printf("insert before %d:\n", indx);  sl->printList();

	char* elem = "ninja"; 
	indx = sl->findIndex(elem);
	if (indx < 0) printf("Index of %s is not found\n", elem);
	else printf("Index of %s is %d\n", elem, indx);

	return 0;		
} 

