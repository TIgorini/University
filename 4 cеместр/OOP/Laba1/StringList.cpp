/*!
 * file: StringList.cpp
 * StringList class implementation
 * written: 15/03/2017
 */

#include "StringList.h"

void StringList::StringList(){

	StringList = new ListNode;
}

void StringList::~StringList(){

	delete[];
}