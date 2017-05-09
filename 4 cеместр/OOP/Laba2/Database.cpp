/*!
* file: Database.cpp
* written: 16/04/2017
* synopsis: Database class implementation
* author: Igor Tymoshenko
*/

#include "Database.h"
// Database Class Implemantation

Database::Database(){}
bool Database::loadFromFile(const char* file){

	FILE* f = fopen(file,"r"); 
	if (f == NULL)
		return false;

	int e_m;
	int id;
	string f_name; 
	string l_name;
	int age;
	string dep;
	int sol;

	int buf;	
	while (!feof(f)){

		fscanf(f,"%d;%d;", &e_m, &id); 
		while ( (buf = fgetc(f)) != ';') 
			f_name.append(1, buf);
		while ( (buf = fgetc(f)) != ';')
			l_name.append(1, buf);	
		fscanf(f,"%d;", &age); 
		while ( (buf = fgetc(f)) != ';')
			dep.append(1, buf);		
		fscanf(f,"%d", &sol); 		 
		
		if ( e_m == 0 ){
			Employee emp(f_name, l_name, age, id);
			emp.setSalary(sol);
			emp.setDepartment(dep); 
			employees.push_back(new Employee(emp)); 
		}
		else{
			Manager man(f_name, l_name, age, id);
			man.setSalary(sol);
			man.setDepartment(dep);
			employees.push_back(new Manager(man));
		}
		f_name.clear();
		l_name.clear();
		dep.clear();
	}
	fclose(f);
	return true;	 
}

void Database::arrangeSubordinates(){

	for (vector<Person*>::iterator it = employees.begin(); it != employees.end(); it++){

		if (typeid(**it) == typeid(Manager)){
			string dept = dynamic_cast<Manager*>(*it)->getDepartment();
			for (vector<Person*>::iterator it_two = employees.begin(); it_two != employees.end(); it_two++)

				if ((typeid(**it_two) == typeid(Employee)) && (dept == dynamic_cast<Employee*>(*it_two)->getDepartment()))
					dynamic_cast<Manager*>(*it)->addSubordinate(*it_two);
		}
	}
}

void Database::clearSubordinates(){

	for (vector<Person*>::iterator it = employees.begin(); it != employees.end(); it++)
		if (typeid(**it) == typeid(Manager))
			dynamic_cast<Manager*>(*it)->freeSubordinates();

}

Person* Database::hireEmployee(Person *p){

	employees.push_back(p);
	clearSubordinates();
	arrangeSubordinates();
	return p;
}

void Database::displayDepartmentEmployees(string _department){

	for (vector<Person*>::iterator it = employees.begin(); it != employees.end(); it++)
		if (_department == dynamic_cast<Employee*>(*it)->getDepartment())
			(*it)->display();

}

bool Database::fireEmployee(int id){

	for (vector<Person*>::iterator it = employees.begin(); it != employees.end(); it++)
		if (id == dynamic_cast<Employee*>(*it)->getId()){
			employees.erase(it);
			clearSubordinates();
			arrangeSubordinates();
			return 0;
		}
	return 1;	
}

void Database::displayAll(){

	for (vector<Person*>::iterator it = employees.begin(); it != employees.end(); it++)
		(*it)->display();
}
