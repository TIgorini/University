#pragma once
#include <string>
#include <iostream>
#include <iomanip> 

using namespace std;

class Node {
public:
	string english;
	string italian;
	int page;
	Node *left;
	Node *right;

	Node(string english, string italian, int page);
	void print();
};