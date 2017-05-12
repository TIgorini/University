
run : laba4.o CyclicBuffer.o
	g++ -o run laba4.o CyclicBuffer.o -lpthread

laba4.o : laba4.cpp
	g++ -c laba4.cpp
CyclicBuffer.o : CyclicBuffer.cpp CyclicBuffer.h
	g++ -c CyclicBuffer.cpp

clean:
	rm -fv CyclicBuffer.o laba4.o
				