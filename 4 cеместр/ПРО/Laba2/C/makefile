
lab2: main.o input.o output.o search.o
	gcc -o lab2 main.o input.o output.o search.o

main.o : main.c types.h
	gcc -c main.c
input.o : input.c input.h
	gcc -c input.c
output.o : output.c output.h
	gcc -c output.c
search.o : search.c search.h
	gcc -c search.c

clean :
	rm -fv lab2 *.o 	
