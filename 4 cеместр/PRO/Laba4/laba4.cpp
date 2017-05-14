#include <stdio.h>
#include <pthread.h>
#include <semaphore.h>
#include <unistd.h>
#include "CyclicBuffer.h"

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
		if (sem_val < SIZE - 1){
						
			while (pthread_mutex_trylock(&mut_cr) != 0){
				//do some useful work
			}

			fprintf(log, "\tP%d lock mutex\n", curr);
			fprintf(log,"P%d put %d\n",curr, cr->put());
			pthread_mutex_unlock(&mut_cr);
			fprintf(log, "\tP%d unlock mutex\n", curr);
			
			sem_post(&sem_cr);
		}
		usleep(1);		
	}

	return NULL;
} 


void* consumFunc1(void* no_arg){

	fprintf(log, "C1 started\n");
	while (1){

		fprintf(log,"\tC1 send to C2\n");
		sem_post(&sem2);
		fprintf(log,"\tC1 wait C2\n");
		sem_wait(&sem1);

		if (sem_trywait(&sem_cr) == 0){

			while (pthread_mutex_trylock(&mut_cr) != 0){
				//do some useful work
			}

			fprintf(log,"\tC1 lock mutex\n");
			fprintf(log,"C1 get %d\n", cr->get());
			pthread_mutex_unlock(&mut_cr);
			fprintf(log,"\tC1 unlock mutex\n");

		}else
			break;
	}

	pthread_cancel(p1);
	pthread_cancel(p2);
	pthread_cancel(p3);
	return NULL;
}


void* consumFunc2(void* no_arg){

	fprintf(log, "C2 started\n");
	while (1){

		fprintf(log,"\tC2 send to C1\n");
		sem_post(&sem1);
		fprintf(log,"\tC2 try to wait C1\n");
		while (sem_trywait(&sem2) != 0){
			//do some useful work
		}

		if (sem_trywait(&sem_cr) == 0){

			while (pthread_mutex_trylock(&mut_cr) != 0){
				//do some useful work
			}

			fprintf(log,"\tC2 lock mutex\n");
			fprintf(log,"C2 get %d\n", cr->get());
			pthread_mutex_unlock(&mut_cr);
			fprintf(log,"\tC2 unlock mutex\n");	
			
		}else
			break;			
	}
 	
 	pthread_cancel(p1);
	pthread_cancel(p2);
	pthread_cancel(p3);
	return NULL;
}


int main(){

	log = fopen("log.txt","w");
	fprintf(log, "Main started\n");

	sem_init(&sem_cr,0,0);
	sem_init(&sem1,0,0);
	sem_init(&sem2,0,0);
	cr = new CyclicBuffer();

	int id_p1 = 1;
	int id_p2 = 2;
	int id_p3 = 3;

	pthread_create(&p1, NULL, &prodFunc, &id_p1);
	pthread_create(&p2, NULL, &prodFunc, &id_p2);
	pthread_create(&p3, NULL, &prodFunc, &id_p3);
	pthread_create(&c1, NULL, &consumFunc1, NULL); 
	pthread_create(&c2, NULL, &consumFunc2, NULL);

	pthread_join(p1, NULL); 	fprintf(log, "P1 stoped\n");
	pthread_join(p2, NULL); 	fprintf(log, "P2 stoped\n");
	pthread_join(p3, NULL); 	fprintf(log, "P3 stoped\n");
	pthread_join(c1, NULL);		fprintf(log, "C1 stoped\n");
	pthread_join(c2, NULL);		fprintf(log, "C2 stoped\n");

	delete cr;
	fprintf(log, "Main stoped\n");
	fclose(log);
	return 0;
}
