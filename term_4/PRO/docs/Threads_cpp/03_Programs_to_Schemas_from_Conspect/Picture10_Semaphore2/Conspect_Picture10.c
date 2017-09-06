#include <pthread.h>
#include <stdio.h>
#include <unistd.h>
#include <semaphore.h>

//  Copyright (c) 2009 Oleksandr Marchenko. All rights reserved.

// Програма до схеми, показаної у конспекті на рис.10
// (повна синхронізація за допомогою двох двійкових семафорів)

int m = 75;
int n = 100;

/* Оголошення семафорів. */
sem_t  sem1;
sem_t  sem2;


/* Потокова функція1. */
void* thread_function1(void* unused)
{
  int i;
  /* Виведення на екран символу 'а' n разів. */
  for (i = 0; i < n; i++)
  {
    printf("a ");
  }
  
  printf("\nthread_function1 opens semaphore sem2 for the thread_function2\n");
  /* Функція відкриває семафор sem2 для потоку thread_function2 */
  sem_post(&sem2);
  printf("\nSemaphore sem2 is opened!\n");

  printf("\nthread_function1 waits for the opening of the semaphore sem1\n");
  /* Функція чекає, поки потік thread_function2 відкриє семафор sem1. */
  sem_wait(&sem1);

  printf("\nthread_function1 works after semaphore sem1\n");
  /* Виведення на екран символу 'b' n разів. */
  for (i = 0; i < n; i++)
  {
    printf("b ");
  }
  printf("\n");
  return NULL;
}

/* Потокова функція2. */
void* thread_function2(void* unused)
{
  int i;
  /* Виведення на екран символу '1' m разів. */
  for (i = 0; i < m; i++)
  {
    printf("1 ");
  }
  
  printf("\nthread_function2 opens semaphore sem1 for the thread_function1\n");
  /* Функція відкриває семафор sem1 для потоку thread_function1 */
  sem_post(&sem1);
  printf("\nSemaphore sem1 is opened!\n");

  printf("\nthread_function2 waits for the opening of the semaphore sem2\n");
  /* Функція чекає, поки потік thread_function1 відкриє семафор sem2. */
  sem_wait(&sem2);

  printf("\nthread_function2 works after semaphore sem2\n");
  /* Виведення на екран символу '2' m разів. */
  for (i = 0; i < m; i++)
  {
    printf("2 ");
  }
  printf("\n");
  return NULL;
}

int main()
{
  /* Оголошуються потокові змінні. */
  pthread_t thread1;
  pthread_t thread2;

  /*Ініціалізуються семафори. */
  sem_init(&sem1,0,0);
  sem_init(&sem2,0,0);

  /* Створюються потоки. */
  pthread_create (&thread1, NULL, &thread_function1, NULL);
  pthread_create (&thread2, NULL, &thread_function2, NULL);

  /* Очікується завершення потоків. */
  pthread_join (thread1, NULL);
  pthread_join (thread2, NULL);

  /*Завершення програми. */
  return 0;
}
