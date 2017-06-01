#include "BinTree.h"

int main(){

	BinTree dic;
	if (dic.loadFromFile("words.csv") != 0)
		printf("Can't find file");

	int key;
	int page;
	string eng;
	string ital;
	Node* word;
	do
	{	
		cout << endl;
		cout << "===========================" << endl;
		cout << " Operations: " << endl;
		cout << " 1. Display all" << endl;
		cout << " 2. Display page" << endl;
		cout << " 3. Add word" << endl;
		cout << " 4. Remove word" << endl;
		cout << " 5. Find" << endl;
		cout << " 0. Exit" << endl;
		cout << "===========================" << endl;
		cout << "Enter operation number: ";
		cin >> key;
		
		switch (key){
		case 1:
			cout << endl << "English\t    Italian\tPage" << endl;
			cout << "------------------------------" << endl;
			dic.print(dic.root);
			cout << "------------------------------" << endl;
			break;
		case 2:
			cout << "Enter page to view: ";
			cin >> page;
			cout << endl << "English\t    Italian\tPage" << endl;
			cout << "------------------------------" << endl;
			dic.printPage(dic.root, page);
			cout << "------------------------------" << endl;
			break;
		case 3:
			cout << "Enter english word: ";
			cin >> eng;
			if ( dic.find(dic.root, eng) != NULL)
				cout << "Word \"" << eng << "\" is already exist" << endl;
			else{		
				cout << "Enter italian translate: ";
				cin >> ital;
				cout << "Enter page: ";
				cin >> page;
				dic.add(new Node(eng, ital, page));
				cout << "Word \"" << eng << "\" added" << endl;
			}
			break;
		case 4:
			cout << "Enter word to remove: ";
			cin >> eng;
			if ( dic.remove(eng) != 0)
				cout << "No such word" << endl;
			else
				cout << "Word \"" << eng << "\" removed" << endl; 
			break;
		case 5:
			cout << "Write english word: ";
			cin >> eng;
			word = dic.find(dic.root, eng);
			if (word == NULL)
				cout << "Can't find this word" << endl;
			else{
				cout << endl << "English\t    Italian\tPage" << endl;
				cout << "------------------------------" << endl;
				word->print();
				cout << "------------------------------" << endl;
			}
			break;	
		case 0:
			return 0;
		default:
			cout << "I don't know this command" << endl;
			break;
		}
	} while (true);

	return 0;
}