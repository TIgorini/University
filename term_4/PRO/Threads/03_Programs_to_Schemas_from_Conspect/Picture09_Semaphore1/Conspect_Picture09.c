#include <pthread.h>
#include <stdio.h>
#include <unistd.h>
#include <semaphore.h>

//  Copyright (c) 2009 Oleksandr Marchenko. All rights reserved.

// Часткова (неповна) синхронізація
// за допомогою двійкового семафору

/* Оголошення семафору. */
sem_t  sem1;

/* Потокова функція1. */
void* thread_function1(void* unused)
{
  int i = 0;
  /* Виведення на екран символу 'а' 100 разів. */
  for (i = 0; i<100; i++)
  {
    printf("a ");
  }
  printf("\nthread_function1 waits for the opening of the semaphore...\n");
  /* thread_function1 чекає, поки якийсь інший потік відкриє семафор. */
  sem_wait(&sem1);
  printf("\nSemaphore is opened!\n");
  /* Виведення на екран символу 'b' 100 разів. */
  for (i = 0; i<100; i++)
  {
    printf("b ");
  }
}

/* Потокова функція2. */
void* thread_function2(void* unused)
{

  usleep(10);
  
  int i = 0;
  /* Виведення на екран символу '1' 75 разів. */
  for (i = 0; i<75; i++)
  {
    printf("1 ");
  }
  /* thread_function2 відкриває семафор. */
  sem_post(&sem1);
  printf("\nthread_function2 has opened the semaphore.\n");
  /* Виведення на екран символу '2' 75 разів. */
  for (i = 0; i<75; i++)
  {
    printf("2 ");
  }
}

int main()
{
  /* Оголошуються потокові змінні. */
  pthread_t thread1;
  pthread_t thread2;

  /*Ініціалізується семафор. */
  sem_init(&sem1,0,0);

  /* Створюються потоки. */
  pthread_create (&thread1, NULL, &thread_function1, NULL);
  pthread_create (&thread2, NULL, &thread_function2, NULL);

  /* Очікується завершення потоків. */
  pthread_join (thread1, NULL);
  pthread_join (thread2, NULL);

  /*Завершення програми. */
  return 0;
}

/* Для ілюстрації того випадку, коли друга функція відкриває семафор до того, як перша біля нього зупиниться, потрібно поміняти порядок створення потоків. Через те, що обидві функції реалізують дуже простий алгоритм, прослідкувати паралельність цих потоків дуже важко, Але відстежити синхронізацію потоків можна, як показано в цій програмі. */

