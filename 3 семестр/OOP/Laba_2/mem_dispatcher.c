/***********************************
* file: mem_dispatcher.c
* author: Igor Tymoshenko
* written: 27/11/2016
* last modified: 27/11/2016
* synopsis: realization of memory dispatcher interface
************************************/

#include "mem_dispatcher.h"

//creates a heap as a single free block  with id 0 and HEAP_SIZE size
void init(mem_dispatcher *md) {
	
	mem_chunk *tmp = (mem_chunk *)malloc(sizeof(mem_chunk));
	md->last_id_used = 0;
	md->first = NULL;
	tmp->id = 0;
	tmp->size = HEAP_SIZE;
	tmp->status = FREE;
	tmp->next = NULL;
	md->first = tmp;	
}

//returns block id if allocated and -1 otherwise
int allocate(mem_dispatcher *md, int size) {
	
	mem_chunk *tmp, *buf;
	int minID = -1;
	int minSize;
	if (size > HEAP_SIZE || size <= 0)
		return -1;
	
	tmp = md->first;
	minSize = 10;
	//searching in list a block with min free size
	while (tmp) {
		if ((tmp->status == FREE) && (tmp->size >= size) && (tmp->size <= minSize)) {
			minSize = tmp->size;
			minID = tmp->id;
		}
		tmp = tmp->next;
	}
	if (minID == -1)
		return -1;

	tmp = md->first;
	while (minID != tmp->id) {
		tmp = tmp->next;
	}
	if (size == tmp->size) {
		tmp->status = ALLOCATED;
		return md->last_id_used;
	}
	tmp->size -= size;
	
	buf = (mem_chunk *)malloc(sizeof(mem_chunk));
	md->last_id_used++ ;
	buf->id = md->last_id_used;
	buf->size = size;
	buf->status = ALLOCATED;
	buf->next = tmp->next;
	tmp->next = buf;
	
	return md->last_id_used;
}

//returns nonnegative value if block is deallocated and -1 otherwise
int deallocate(mem_dispatcher *md, int block_id) {

	mem_chunk *tmp, *prev;
	tmp = md->first;
	prev = tmp;
	// moving to block with id block_id
	while ( tmp && (tmp->id != block_id) ) {
		prev = tmp;
		tmp = tmp->next;
	}
	if (tmp == NULL || tmp->status == FREE)
		return -1;
	
	tmp->status = FREE;
	// union of adjacent free blocks 
	if ((tmp != prev) && (prev->status == FREE)) {
		prev->size += tmp->size;
		prev->next = tmp->next;
		free(tmp);
		tmp = prev;
	}
	prev = tmp;
	tmp = tmp->next;
	if ((tmp != NULL) && (tmp->status == FREE)) {
		prev->size += tmp->size;
		prev->next = tmp->next;
		free(tmp);
	}

	return 0;
}

//reunites free blocks that were previously stored in various parts of a heap //into one successive block 
void defragment(mem_dispatcher *md) {

	mem_chunk *prev, *tmp;
	int size_free = 0;
	
	tmp = md->first;
	// axception for first free blocks
	while (tmp->status == FREE) {
		md->first = tmp->next;
		size_free += tmp->size;
		free(tmp);
		tmp = md->first;
	}
	prev = tmp;
	while (tmp != NULL) {
		if (tmp->status == FREE) {
			prev->next = tmp->next;
			size_free += tmp->size;
			free(tmp);
			tmp = prev;
		}
		prev = tmp;
		tmp = tmp->next;
	}

	tmp = (mem_chunk *)malloc(sizeof(mem_chunk));
	tmp->id = 0;
	tmp->size = size_free;
	tmp->status = FREE;
	tmp->next = NULL;
	prev->next = tmp;
	
}

//displays heap status
void show_memory_map(mem_dispatcher *md) {

	mem_chunk *tmp = md->first;
	while (tmp) {
		if ( tmp->status == FREE)
			printf("\nblock id: %d\n            size: %d    status: %s\n", tmp->id, tmp->size, "free");
		else
			printf("\nblock id: %d\n            size: %d    status: %s\n", tmp->id, tmp->size, "allocate");
		tmp = tmp->next;
	}
}
