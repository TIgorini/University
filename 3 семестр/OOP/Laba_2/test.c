/***********************************
* file: test.c
* author: Igor Tymoshenko
* written: 27/11/2016
* last modified: 27/11/2016
* synopsis: memory dispatcher testing
************************************/

#include "mem_dispatcher.h"

int main() {
	int buf = 0;
	mem_dispatcher md;
	init(&md);
	do {
		printf("\n1. Allocate\n2. Deallocate\n3. Show memory map\n4. Defragment\n0. Exit\nEnter command : ");
		scanf("%d",&buf);
		switch (buf) {
			case 1:
				printf("Enter block size: ");
				scanf("%d", &buf);
				if (allocate(&md, buf) == -1)
					printf("Operation failed\n");
				else
					printf("Operation comlete\n");
				break;
			case 2:
				printf("Enter block id: ");
				scanf("%d", &buf);
				if (deallocate(&md, buf) == -1)
					printf("Operation failed\n");
				else
					printf("Operation complete\n");
				break;
			case 3:
				show_memory_map(&md);
				break;
			case 4:
				defragment(&md);
				printf("Operation comlete\n");
				break;
			case 0: 
				return 0;
		}
	} while (1);
}