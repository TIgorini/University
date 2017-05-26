#include "CyclicBuffer.h"

CyclicBuffer::CyclicBuffer(){
	w_indx = 0;
	r_indx = 0;
}

CyclicBuffer::~CyclicBuffer(){}

int CyclicBuffer::put(){

	int elem;
	if (w_indx != 0)
		elem = buf[w_indx - 1] + 1;
	else
		elem = buf[SIZE - 1] + 1;

	buf[w_indx] = elem;
	w_indx = (w_indx + 1) % SIZE;
	return elem;
}

int CyclicBuffer::get(){

	int elem = buf[r_indx];
	r_indx = (r_indx + 1) % SIZE;
	return elem;
}
