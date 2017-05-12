/*!
* file: Test.cpp
* written: 16/04/2017
* synopsis: Database class test
* author: Igor Tymoshenko
*/

#include "Database.h"

int main(){
 	
	Database db;
	if ( !db.loadFromFile("employees.csv"))
		cout << "File input error" << endl;
	db.displayAll();
	cout << "ArrangeSubordinates:" << endl;
	db.arrangeSubordinates();
	db.displayAll();
	cout << "IT depatment" << endl;
	db.displayDepartmentEmployees("IT");
	
	cout << endl << "New employee added" << endl << endl;
	Manager man("Igor","Tymoshenko",19,7);
	man.setDepartment("PR");
	man.setSalary(1000);  
	db.hireEmployee(&man);
	db.displayAll();

	cout << endl << "Fire employee" << endl << endl;
	if (db.fireEmployee(4) != 0)
		cout << "fire is faild" << endl;
	if (db.fireEmployee(0) != 0)
		cout << "fire is faild" << endl;
	db.displayAll();

	return 0;
}
