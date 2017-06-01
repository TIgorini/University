#pragma once
#include "TreeNode.h"
#include <stdio.h>

class BinTree{
public:
	BinTree();
	int loadFromFile(const char* file);
	void add(Node* elem);
	int remove(string eng);
	Node* find(Node* cur, string eng);
	void print(Node* root);
	void printPage(Node* root, int page);

	Node* root;

private:
	void recAdd(Node* cur, Node* elem);
	Node* findRem(Node* cur, string eng, Node &perent);
	Node* min(Node* cur, Node **parent);	
};