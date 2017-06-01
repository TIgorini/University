#include "BinTree.h"

BinTree::BinTree(){
	root = NULL;
}

int BinTree::loadFromFile(const char* file){

	FILE* f = fopen(file,"r"); 
	if (f == NULL)
		return 1;

	string eng;
	string ital;
	int page;
	char buf;
	while (!feof(f)){
		while ( (buf = fgetc(f)) != ';') 
			eng.append(1, buf);
		while ( (buf = fgetc(f)) != ';') 
			ital.append(1, buf);
		fscanf(f,"%d\n", &page);
		
		add(new Node(eng, ital, page));
		eng.clear();
		ital.clear();
	}
	fclose(f);
	return 0;
}


void BinTree::recAdd(Node* cur, Node* elem){

	if (elem->english < cur->english){
		if (cur->left == NULL){
		 	cur->left = elem;  
			return;
		}
		recAdd(cur->left, elem);
	}else{
		if (cur->right == NULL){
		 	cur->right = elem;  
			return;
		}
		recAdd(cur->right, elem);
	}
}

void BinTree::add(Node* elem){

	if (root == NULL)
		root = elem;
	else if ( find(root, elem->english) == NULL)
		recAdd(root, elem);
}


Node* BinTree::findRem(Node* cur, string eng, Node **parent){

	if (cur == NULL || eng == cur->english)
      	return cur;

    *(parent) = cur;
   	if (eng < cur->english)
      	return findRem(cur->left, eng, parent);
   	else
      	return findRem(cur->right, eng, parent);
}

Node* BinTree::min(Node* cur, Node **parent){

	if (cur->left == NULL)
		return cur;

	*parent = cur;
	return min(cur->left, parent);
}

int BinTree::remove(string eng){

	Node* buf;
	Node* parent;
	Node* rem = findRem(root, eng, &parent);
	if (rem == NULL)
		return 1;

	if (parent->left == rem){
		if (rem->left == NULL && rem->right == NULL){
			parent->left = NULL;
			delete rem;
		}
		else if (rem->left == NULL){
			buf = rem->right;
			parent->left = buf;
			delete rem;
		}
		else if (rem->right == NULL){
			buf = rem->left;
			parent->left = buf;
			delete rem;
		}
		else{
			buf = min(rem->right, &parent);
			rem->english = buf->english;
			rem->italian = buf->italian;
			rem->page = buf->page;
			parent->left = NULL;
			delete buf;
		}
	}else{
		if (rem->left == NULL && rem->right == NULL){
			parent->right = NULL;
			delete rem;
		}
		else if (rem->left == NULL){
			buf = rem->right;
			parent->right = buf;
			delete rem;
		}
		else if (rem->right == NULL){
			buf = rem->left;
			parent->right = buf;
			delete rem;
		}
		else{
			buf = min(rem->right, &parent);
			rem->english = buf->english;
			rem->italian = buf->italian;
			rem->page = buf->page;
			parent->left = NULL;
			delete buf;
		}
	}
	return 0;
}


Node* BinTree::find(Node* cur, string eng){

   	if (cur == NULL || eng == cur->english)
      	return cur;

   	if (eng < cur->english)
      	return find(cur->left, eng);
   	else
      	return find(cur->right, eng);
}


void BinTree::print(Node* root){
	
	if (root == NULL)
		return;

	print(root->left);
	root->print();
	print(root->right);
}

void BinTree::printPage(Node* root, int page){

	if (root == NULL)
		return;

	printPage(root->left, page);
	if (root->page == page)
		root->print();
	printPage(root->right, page);
}