/*!
* file: Employee.cpp
* written: 16/04/2017
* synopsis: Employee class implementation
* author: Igor Tymoshenko
*/

#include "Employee.h"
//Classes Implemantation

Employee::Employee(const Employee &e){

	f_name = e.f_name;
	l_name = e.l_name;
	age = e.age;
	salary = e.salary;
	id = e.id;
	department = e.department; 
}
Employee& Employee::operator=(const Employee &e){

	f_name = e.f_name;
	l_name = e.l_name;
	age = e.age;
	salary = e.salary;
	id = e.id;
	department = e.department;
}
void Employee::setSalary(int s) { salary = s; }
void Employee::setDepartment(string dept) { department = dept; }
void Employee::setId(int n) { id = n; }

int Employee::getId() { return id; }
string Employee::getDepartment() { return department; }
string Employee::getFirstName() { return f_name; }
string Employee::getLastName() { return l_name; }
int Employee::getAge() { return age; }
int Employee::getSalary() { return salary; }

void Employee::display(){

	cout << "Employment type: employee" << endl;
	cout <<	"id: " << id << endl;
	cout << f_name << " " << l_name << " ";
	cout << "  age: " << age << "  salary: " << salary << endl;
	cout << "department: " << department << endl << endl << endl;
}