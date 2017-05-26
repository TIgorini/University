/*!
* file: Manager.h
* written: 16/04/2017
* synopsis: Manager class declaration
* author: Igor Tymoshenko
*/

#pragma once
#include "Employee.h"
#include <list>
#include <typeinfo>

class Manager : public Employee {
public:
	Manager(){};
	Manager(string _f_name, string _l_name, int _age, int _id) : 
		Employee(_f_name, _l_name, _age, _id){};
	Manager(const Manager &m);
	Manager& operator=(const Manager &m);
	virtual void display();
	//add an employee to the subordinates list
	Person* addSubordinate(Person *p);
	void freeSubordinates();
	void displaySubordinates();

private:
	list<Person*> subordinates;//список подчиненных	
};