#include "TreeNode.h"

Node::Node(string english, string italian, int page){
	this->english = english;
	this->italian = italian;
	this->page = page;
	left = NULL;
	right = NULL;	
}

void Node::print(){
	cout << setw(13) << std::left << english;
	cout << setw(13) << std::left << italian;
	cout << setw(13) << std::left << page << endl;
}