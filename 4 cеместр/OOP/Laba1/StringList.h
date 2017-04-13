/*!
 * file: StringList.h
 * StringList class declaration
 * written: 15/03/2017
 */

#pragma once
#include <string.h>
#include <stdio.h>

struct ListNode{
	char *str;
	ListNode* next;
	ListNode* prev;
};

typedef const ListNode* POSITION;

class StringList{
public:
	 StringList(void);
	~StringList(void);

		//Head/Tail Access 
	ListNode* getHead() const;
	ListNode* getTail() const; 
	
		//Operations
	void addHead(const char *);
	void addHead(const StringList *);
	void addTail(const char *);
	void addTail(const StringList *);
	void removeAll();
	void removeHead();
	void removeTail();
	void appendExclusively(const StringList *);
	void splice(POSITION where, StringList* sl, POSITION first, POSITION last);
	void unique();

		//Iteration 
	POSITION getNext();
	POSITION getPrev();
	
		//Retrieval/Modification
	POSITION getHeadPosition();

	const char* getAt(int )const;
	void removeAt(int );
	void setAt(char *, int );

		//Insertion 
	void insertAfter(char *, int);
	void insertBefore(char *, int);

		//Searching 
	const ListNode* find(char *);
	int findIndex(char *)const;

		//Status 
	int getSize()const;
	bool isEmpty()const;

	void printNode( const ListNode *p);
	void printList();

private:
	ListNode* head;
	ListNode* tail;
	POSITION iterator;
	int size;
};