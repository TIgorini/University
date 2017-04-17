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
    Database(){};
    //creates “flat” database
    bool LoadFromFile(const char *file);
    //arranges "flat" database after loading from the file
    void ArrangeSubordinates();
    //hire a new employee
    Person* HireEmployee(Person *p);
    void DisplayDepartmentEmployees(string _department);    
    //fire the employee
    bool FireEmployee(int id);
    void DisplayAll();

    //Add here whatever you need

private:
    vector<Person*> employees;
};