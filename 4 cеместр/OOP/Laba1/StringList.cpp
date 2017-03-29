/*!
 * file: StringList.cpp
 * StringList class implementation
 * written: 15/03/2017
 */

#include "StringList.h"

StringList::StringList(){

	head = NULL;
	tail = head;
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

ListNode* StringList::getHead() const {return head;}
ListNode* StringList::getTail() const {return tail;}

void StringList::addHead(const char* str){

	ListNode* buf = new ListNode;
	buf->str = (char*)str;
	buf->next = head;
	buf->prev = NULL;
	if (head != NULL)
		head->prev = buf;
	head = buf;
	size++;
}
void StringList::addHead(const StringList* existList){

	ListNode* buf = existList->getTail();
	while (buf != NULL){
		addHead(buf->str);
		buf = buf->prev;
	}
	size += existList->getSize();
}

void StringList::addTail(const char* str){

	ListNode* buf = new ListNode;
	buf->str = (char*)str;
	buf->next = NULL;
	buf->prev = tail;
	if (tail != NULL)
		tail->next = buf;
	tail = buf;
	size++;
}
void StringList::addTail(const StringList* existList){

	ListNode* buf = existList->getHead();
	while (buf != NULL){
		addTail(buf->str);
		buf = buf->next;
	}
	size += existList->getSize();
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

	ListNode* buf = head->next;
	buf->prev = NULL;
	delete head;
	head = buf;
	size--;
}
void StringList::removeTail(){

	ListNode* buf = tail->prev;
	buf->next = NULL;
	delete tail;
	tail = buf;	
	size--;
}

//void StringList::





int StringList::getSize() const {return size;}
bool StringList::isEmpty()const {return (size == 0) ? 1 : 0; }

void StringList::printNode(const ListNode *p) {printf("%s\n", p->str);}