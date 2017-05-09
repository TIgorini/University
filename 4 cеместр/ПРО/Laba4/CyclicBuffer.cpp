#include <CyclicBuffer.h>

CyclicBuffer::CyclicBuffer(){

	buf = new int[SIZE];
	w_indx = 0;
	r_indx = 0;
	el_count = 0;
}

CyclicBuffer::~CyclicBuffer() { delete buf; }

void CyclicBuffer::put(){

	if (w_indx != 0)
		buf[w_indx] = buf[w_indx - 1] + 1;
	else
		buf[w_indx] = buf[SIZE - 1] + 1;

	w_indx = (w_indx + 1) % SIZE;
	el_count++;
}

int CyclicBuffer::get(){

	int elem = buf(r_indx);
	r_indx = (r_indx + 1) % SIZE;
	el_count--;
	return elem;
}

bool CyclicBuffer::isFull(){

	if (el_count == SIZE)
		return true;
	else
		return false;
}

bool CyclicBuffer::isEmpty(){

	if (el_count == 0)
		return true;
	else
		return false;
}