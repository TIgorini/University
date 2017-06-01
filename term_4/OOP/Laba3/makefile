
run: Dictionary.o BinTree.o TreeNode.o
	g++ -o run Dictionary.o BinTree.o TreeNode.o

Dictionary.o: Dictionary.cpp
	g++ -c Dictionary.cpp

BinTree.o: BinTree.cpp BinTree.h
	g++ -c BinTree.cpp

TreeNode.o: TreeNode.cpp TreeNode.h
	g++ -c TreeNode.cpp


clear: 
	rm -fv Dictionary.o BinTree.o TreeNode.o
