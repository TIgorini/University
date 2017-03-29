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
//	void appendExclusively(const StringList *);
//	void splice(POSITION where, StringList *sl, POSITION first, POSITION last);
//	void unique();

		//Iteration 
//	POSITION GetNext();
//	POSITION ListNode* GetPrev();
	
		//Retrieval/Modification
//	POSITION ListNode* GetHeadPosition();

//	const char* GetAt(int )const;
//	void RemoveAt(int );
//	void SetAt(char *, int );

		//Insertion 
//	void InsertAfter(char *, int);
//	void InsertBefore(char *, int);

		//Searching 
//	const ListNode* Find(char *);
//	int FindIndex(char *)const;

		//Status 
	int getSize()const;
	bool isEmpty()const;

	void printNode( const ListNode *p);

private:
	ListNode* head;
	ListNode* tail;
	int size;
};