#include <stdio.h>
#include <iostream>
#include <pthread.h>
#include <semaphore.h>
#include <unistd.h>
#include "CyclicBuffer.h"

using namespace std;

FILE *log;
CyclicBuffer* cr;
pthread_mutex_t mut_cr = PTHREAD_MUTEX_INITIALIZER;
sem_t sem_cr;
sem_t sem1;
sem_t sem2;

pthread_t p1;
pthread_t p2;
pthread_t p3;
pthread_t c1;
pthread_t c2;


void* prodFunc(void* arg){

	int curr = *(int*)arg;
	int sem_val; 
	fprintf(log, "P%d started\n", curr);

	while (1){

		sem_getvalue(&sem_cr, &sem_val);
		if (sem_val == 0)
			break;

		sem_getvalue(&sem_cr, &sem_val);
		if (sem_val < SIZE - 2){
			
			fprintf(log, "\tP%d try lock mutex\n", curr);			
			if (pthread_mutex_trylock(&mut_cr) == 0){
			
				//fprintf(log, "\tP%d lock mutex\n", curr);
				fprintf(log,"P%d put %d\n",curr, cr->put());
				//fprintf(log, "\tP%d unlock mutex\n", curr);
				pthread_mutex_unlock(&mut_cr);
				sem_post(&sem_cr);
				//sem_getvalue(&sem_cr, &sem_val);
				//fprintf(log, "\t\tP%d inc sem (%d)\n", curr, sem_val);
			}else
				fprintf(log,"\tP%d do some work\n",curr);
		}		
		
	}

	pthread_cancel(c1);
	pthread_cancel(c2);
	fprintf(log, "P%d stoped\n", curr);	
	return NULL;
} 


void* consumFunc1(void* no_arg){

	int sem_val; 
	fprintf(log, "C1 started\n");

	while (1){

		fprintf(log,"\tC1 send to C2\n");
		sem_post(&sem2);
		fprintf(log,"\tC1 wait C2\n");
		sem_wait(&sem1);

		if (sem_trywait(&sem_cr) == 0){
			//sem_getvalue(&sem_cr, &sem_val);
			//fprintf(log,"\t\tC1 dec sem (%d)\n",sem_val);

			//fprintf(log,"\tC1 try lock mutex\n");
			if (pthread_mutex_trylock(&mut_cr) == 0){

				//fprintf(log,"\tC1 lock mutex\n");
				fprintf(log,"C1 get %d\n", cr->get());
				pthread_mutex_unlock(&mut_cr);
				//fprintf(log,"\tC1 unlock mutex\n");
			}
			else
				fprintf(log,"\tC1 do some work\n");

		}else
			break;
	}

	fprintf(log, "C1 stoped\n");
	return NULL;
}


void* consumFunc2(void* no_arg){

	int sem_val; 
	fprintf(log, "C2 started\n");

	while (1){

		fprintf(log,"\tC2 send to C1\n");
		sem_post(&sem1);
		fprintf(log,"\tC2 try to wait C1\n");
		if (sem_trywait(&sem2) == 0){

			if (sem_trywait(&sem_cr) == 0){
				//sem_getvalue(&sem_cr, &sem_val);
				//fprintf(log,"\t\tC2 dec sem (%d)\n", sem_val);

				fprintf(log,"\tC2 try lock mutex\n");
				if (pthread_mutex_trylock(&mut_cr) == 0){

					//fprintf(log,"\tC2 lock mutex\n");
					fprintf(log,"C2 get %d\n", cr->get());
					pthread_mutex_unlock(&mut_cr);
					//fprintf(log,"\tC2 unlock mutex\n");
				}
				else
					fprintf(log,"\tC2 do some work\n");
			
			}else
				break;
		}else
			fprintf(log,"\tC2 do some work\n");			
	}

	fprintf(log, "C2 stoped\n");
	return NULL;
}


int main(){

	log = fopen("log.txt","w");
	fprintf(log, "Main started\n");

	sem_init(&sem_cr,0,0);
	sem_init(&sem1,0,0);
	sem_init(&sem2,0,0);
	cr = new CyclicBuffer();

	for (int i = 0; i < 3; i++){
		cr->put();
		sem_post(&sem_cr);
	}
	fprintf(log, "Created first 3 elements of CR: 1, 2, 3\n");

	int id_p1 = 1;
	int id_p2 = 2;
	int id_p3 = 3;

	pthread_create(&p1, NULL, &prodFunc, &id_p1);
	pthread_create(&p2, NULL, &prodFunc, &id_p2);
	pthread_create(&p3, NULL, &prodFunc, &id_p3);
	pthread_create(&c1, NULL, &consumFunc1, NULL); 
	pthread_create(&c2, NULL, &consumFunc2, NULL);

	pthread_join(p1, NULL);
	pthread_join(p2, NULL);
	pthread_join(p3, NULL);
	pthread_join(c1, NULL);
	pthread_join(c2, NULL);

	delete cr;
	fprintf(log, "Main stoped\n");
	fclose(log);
	return 0;
}
