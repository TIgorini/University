/*!
 * file: StringList.cpp
 * StringList class implementation
 * written: 15/03/2017
 */

#include "StringList.h"

StringList::StringList(){

	head = NULL;
	tail = NULL;
	size = 0;
}

StringList::~StringList(){

	ListNode* buf;
	while (head != NULL){
		buf = head->next;
		delete head;
		head = buf;
	}
}

ListNode* StringList::getHead()const {return head; } 
ListNode* StringList::getTail()const {return tail; }

void StringList::addHead(const char* str){

	ListNode* buf = new ListNode;
	buf->str = (char*)str;
	buf->prev = NULL;
	if (head != NULL){
		head->prev = buf;
		buf->next = head;
	}
	else{
		buf->next = NULL;
		tail = buf;
	}
	head = buf;
	size++;
}

void StringList::addHead(const StringList* existList){

	ListNode* buf = existList->getTail();
	while (buf != NULL){
		addHead(buf->str);
		buf = buf->prev;
	}
}

void StringList::addTail(const char* str){

	ListNode* buf = new ListNode;
	buf->str = (char*)str;
	buf->next = NULL;
	if (tail != NULL){
		tail->next = buf;
		buf->prev = tail;
	}
	else{
		buf->prev = NULL;
		head = buf;
	}	
	tail = buf;
	size++;
}
void StringList::addTail(const StringList* existList){

	ListNode* buf = existList->getHead();
	while (buf != NULL){
		addTail(buf->str);
		buf = buf->next;
	}
}

void StringList::removeAll(){

	ListNode* buf;
	while (head != NULL){
		buf = head->next;
		delete head;
		head = buf;
	}
	size = 0;
}
void StringList::removeHead(){

	if (head != NULL){
		ListNode* buf = head->next;
		buf->prev = NULL;
		delete head;
		head = buf;
		size--;
	}	
}
void StringList::removeTail(){

	if (tail != NULL){
		ListNode* buf = tail->prev;
		buf->next = NULL;
		delete tail;
		tail = buf;	
		size--;
	}
}

void StringList::appendExclusively(const StringList *existList){
	
	ListNode *ptr;
	ListNode *cur = existList->getHead();
	bool f;

	while (cur != NULL){
		ptr = head;
		f = false;
		while (ptr != NULL){
			if (strcmp(cur->str, ptr->str) == 0){
				f = true;
				break;
			}
			ptr = ptr->next;	
		}
		if (f == false)
			addTail(cur->str);
		cur = cur->next;
	}
}
void StringList::splice(POSITION where, StringList* sl, POSITION first, POSITION last){

	if (where == NULL) return;
	if (where->next == NULL){
		while (first != last->next && first != NULL){
			addTail(first->str);
			first = first->next;
			sl->removeAt(sl->findIndex(first->prev->str));
		}	
	}else{
		int i = 0;
		while (first != last->next && first != NULL){
			insertAfter(first->str, findIndex(where->str) + i);
			first = first->next;
			sl->removeAt(sl->findIndex(first->prev->str));
			i++;
		}
	}
}
void StringList::unique(){

	ListNode* cur = head;
	ListNode* ptr;
	bool f;
	 
	while (cur != NULL){
		ptr = head;
		f = false;
		while (ptr != NULL){
			if ((strcmp(cur->str, ptr->str) == 0) && (ptr != cur)){
				f = true;
				break;
			}
			ptr = ptr->next;
		}
		cur = cur->next;
		if (f == true)
			removeAt(findIndex(cur->prev->str));
	}
}

POSITION StringList::getNext(){return iterator = iterator->next; }
POSITION StringList::getPrev(){return iterator = iterator->prev; }
POSITION StringList::getHeadPosition(){return iterator = head; }

const char* StringList::getAt(int indx)const{

	ListNode* cur = getHead();
	int i = 0;
	while (cur != NULL){
		if (i == indx)
			return cur->str;
		cur = cur->next;
		i++;
	}
	return NULL;
}
void StringList::removeAt(int indx){
	
	if (indx < 0 || indx >= size){
		printf("Index is out of range\n");
		return;
	}

	ListNode* cur = head;
	int i = 0;
	if (indx == 0){
		removeHead();
		return;
	}

	while (cur != tail){
		if (i == indx){
			cur->next->prev = cur->prev;
			cur->prev->next = cur->next;
			delete cur;
			size--;
			return;
		}
		cur = cur->next;
		i++;		
	}
	removeTail();
}
void StringList::setAt(char* str, int indx){
	
	ListNode* cur = head;
	int i = 0;
	while (cur != NULL){
		if (i == indx){
			cur->str = str;
			return;
		}
		cur = cur->next;
		i++;
	}
	printf("Index is out of range\n");
}

void StringList::insertAfter(char* str, int indx){
	
	if (indx >= size || indx < 0){
		printf("Index is out of range\n");
		return;
	}

	ListNode* cur = head;
	ListNode* buf = new ListNode;
	int i = 0;
	buf->str = str;
	while (cur != tail){
		if (i == indx){
			buf->next = cur->next;
			cur->next->prev = buf;
			buf->prev = cur;
			cur->next = buf;
			size++;
			return;
		}
		cur = cur->next;
		i++;
	}
	addTail(str);
}
void StringList::insertBefore(char* str, int indx){
	
	if (indx >= size || indx < 0){
		printf("\nIndex is out of range\n");
		return;
	}
	if (indx == 0)
		addHead(str);
	else{
		ListNode *cur = head;
		ListNode *buf = new ListNode;
		int i = 0;
		buf->str = str;
	
		while (cur != NULL){
			if (i == indx){
				buf->next = cur;
				cur->prev->next = buf;
				buf->prev = cur->prev;
				cur->prev = buf;
				size++;
				return;
			}
			cur = cur->next;
			i++;
		}
	}		
}

POSITION StringList::find(char* str){

	POSITION cur = head;
	while (cur != NULL){
		if (strcmp(cur->str, str) == 0)
			return cur;
		cur = cur->next;
	}
	return NULL;
}
int StringList::findIndex(char* str)const{

	POSITION cur = head;
	int indx = 0;
	while (cur != NULL){
		if (strcmp(cur->str, str) == 0){
			return indx;
		}
		cur = cur->next;
		indx++;
	}
	return -1;
}

int StringList::getSize() const {return size;}
bool StringList::isEmpty()const {return (size == 0) ? 1 : 0; }

void StringList::printNode(const ListNode *p) {printf("%s ", p->str); }
void StringList::printList(){

	for (POSITION pos = getHeadPosition(); pos != NULL; pos = getNext()){
		printNode(pos);
	}
	printf("\nSize: %d\n\n",getSize());
}