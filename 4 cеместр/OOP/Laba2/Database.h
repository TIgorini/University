/*!
* file: Database.h
* written: 16/04/2017
* synopsis: Database class definition
* author: Igor Tymoshenko
*/

#pragma once
#include "Manager.h"
#include <vector>

class Database{
public:
    Database();
    //creates “flat” database
    bool loadFromFile(const char *file);
    //arranges "flat" database after loading from the file
    void arrangeSubordinates();
    void clearSubordinates();
    //hire a new employee
    Person* hireEmployee(Person *p);
    void displayDepartmentEmployees(string _department);    
    //fire the employee
    bool fireEmployee(int id);
    void displayAll();

    

private:
    vector<Person*> employees;
};