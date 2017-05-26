
laba4: Test.o Database.o Manager.o Employee.o
	g++ -o laba4 Test.o Database.o Manager.o Employee.o

Test.o: Test.cpp
	g++ -c Test.cpp
Database.o: Database.cpp Database.h
	g++ -c Database.cpp
Manager.o: Manager.cpp Manager.h
	g++ -c Manager.cpp
Employee.o: Employee.cpp Employee.h
	g++ -c Employee.cpp

clear: 
	rm -fv Database.o Test.o Manager.o Employee.o			
