#include <pthread.h>
#include <stdio.h>

//  Copyright (c) 2009 Oleksandr Marchenko. All rights reserved.

void* thread_1 (void* unused)
{
printf("This is thread_1. Its identifier is %lu\n", (unsigned long) pthread_self());
while (1) continue;

return NULL;
}

void* thread_2 (void* unused)
{
printf("This is thread_2. Its identifier is %lu\n", (unsigned long) pthread_self());
while (1) continue;

return NULL;
}

int main()
{
  pthread_t thread_1_id, thread_2_id;

  pthread_create (&thread_1_id, NULL, &thread_1, NULL);
  pthread_create (&thread_2_id, NULL, &thread_2, NULL);

printf("This is main. Its process ID is %d\n", (int) getpid());
printf("This is main. Its thread identifier is %lu\n", (unsigned long) pthread_self());
printf("This is main. Identifier of the thread_1 is %lu\n", (unsigned long) thread_1_id);
printf("This is main. Identifier of the thread_2 is %lu\n", (unsigned long) thread_2_id);

while (1) continue;

return 0;
}
