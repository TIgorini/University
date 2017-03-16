#include <stdio.h>
#include <unistd.h>
#include <pthread.h>

// Copyright (c) 2009 Oleksandr Marchenko. All rights reserved.

void* func1(void* unused)
{
	while (1) 
	{
		printf("thread_1\n",stderr);
		usleep(1);
	}
	return NULL;
}

void* func2(void* unused)
{
	while (1)
	{
		printf("thread_2\n",stderr);
		usleep(1);
	}
	return NULL;
}

int main ()
{
	pthread_t thread_func1;
	pthread_t thread_func2;

	pthread_create(&thread_func1,NULL,&func1,NULL);
	pthread_create(&thread_func2,NULL,&func2,NULL);

	printf("main\n",stderr);

	return 0;
}


