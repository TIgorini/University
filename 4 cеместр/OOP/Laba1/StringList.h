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
	const ListNode* GetHead();
	const ListNode* GetTail(); 
	
		//Operations 
	void AddHead(const char *);
	void AddHead(const StringList *);
	void AddTail(const char *);
	void AddTail(const StringList *);
	void RemoveAll();
	void RemoveHead();
	void RemoveTail();
 	void AppendExclusively(const StringList *);
	void Splice(POSITION where, StringList *sl, POSITION first, POSITION last);
	void Unique();

		//Iteration 
	POSITION GetNext();
	POSITION ListNode* GetPrev();
	
		//Retrieval/Modification
	POSITION ListNode* GetHeadPosition();

	const char* GetAt(int )const;
	void RemoveAt(int );
	void SetAt(char *, int );

		//Insertion 
	void InsertAfter(char *, int);
	void InsertBefore(char *, int);

		//Searching 
	const ListNode* Find(char *);
	int FindIndex(char *)const;

		//Status 
	int Getsize()const;
	bool IsEmpty()const;

	void Printnode( const ListNode *p);

private:
		
};