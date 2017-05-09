/*!
* file: Manager.cpp
* written: 16/04/2017
* synopsis: Manager class implementation
* author: Igor Tymoshenko
*/

#include "Manager.h"

// Manager Class Implemantation

Manager::Manager(const Manager &m){

	f_name = m.f_name;
	l_name = m.l_name;
	age = m.age;
	salary = m.salary;
	id = m.id;
	department = m.department;
}

Manager& Manager::operator=(const Manager &m){

	f_name = m.f_name;
	l_name = m.l_name;
	age = m.age;
	salary = m.salary;
	id = m.id;
	department = m.department;
}

void Manager::display(){

	cout << "Employment type: manager" << endl;
	cout <<	"id: " << id << endl;
	cout << f_name << " " << l_name << " ";
	cout << "  age: " << age << "  salary: " << salary << endl;
	cout << "department: " << department << endl;
	cout << "Subordinates: " << endl;
	displaySubordinates();
}

Person* Manager::addSubordinate(Person *p){
	
	subordinates.push_back(p);
	return p;		
}

void Manager::freeSubordinates(){
	
	subordinates.clear();
}

void Manager::displaySubordinates(){

	if (subordinates.empty())
		cout << "                none" << endl << endl<< endl;
	else{
		for (list<Person*>::iterator it = subordinates.begin(); it != subordinates.end(); it++){

			cout << "         Employment type: employee" << endl;
			cout <<	"         id: " << dynamic_cast<Employee*>(*it)->getId() << endl;
			cout << "         " << dynamic_cast<Employee*>(*it)->getFirstName() << " "; 
			cout << dynamic_cast<Employee*>(*it)->getLastName() << " ";
			cout << "  age: " << dynamic_cast<Employee*>(*it)->getAge(); 
			cout << "  salary: " << dynamic_cast<Employee*>(*it)->getSalary() << endl << endl;
		}
	}
}