#include <fstream>
#include <iostream>
#include <pthread.h>
#include <semaphore.h>
#include "CyclicBuffer.h"

ofstream log;
CyclicBuffer* cr;
pthread_mutex_t mcr = PTHREAD_MUTEX_INITIALIZER;
sem_t scr;
//sem_t sem1;
//sem_t sem2;

void* prodFunc(void* arg){

	int curr = *(int*)arg; 
	log << "Producer " << curr << " started" << endl;

	//while (!cr->isEmpty())


	log << "Producer " << curr << " finished" << endl;	
	return NULL;
} 

void* consumFunc(void* arg){

	int curr = *(int*)arg; 
	log << "Consumer " << curr << " started" << endl;

	log << "Consumer " << curr << " finished" << endl;
	return NULL;
}

int main(){

	log.open("log.txt");
	log << "Main started" << endl;

	cr = new CyclicBuffer();
	for (int i = 0; i < 3; i++)
		cr->put();
	log << "Created first 3 elements of CR: 0, 1, 2" << endl;

	sem_init(&scr,0,0);

	pthread_t p1;	int id_p1 = 1;
	pthread_t c1;	int id_c1 = 1;

	pthread_create(&p1, NULL, &prodFunc, &id_p1);
	pthread_create(&c1, NULL, &consumFunc, &id_c1); 

	pthread_join(p1, NULL);
	pthread_join(c1, NULL);

	delete cr;
	log << "Main finished" << endl;
	log.close();
	return 0;
}
