#include <pthread.h>
#include <stdio.h>
#include <unistd.h>

//  Copyright (c) 2009 Oleksandr Marchenko. All rights reserved.

// Програма до схеми, показаної у конспекті на рис.10.
// Повна синхронізація за допомогою двох сигнальних (умовних) змінних
// (пряма але некоректна синхронізація із-за особливостей реалізації 
// сигнальних (умовних) змінних у ОС Linux)

int n = 100;

/* Оголошення та ініціалізація сигнальних (умовних) змінних. */
pthread_cond_t sig1 = PTHREAD_COND_INITIALIZER;
pthread_cond_t sig2 = PTHREAD_COND_INITIALIZER;

/* Оголошення та ініціалізація м'ютексів. */
pthread_mutex_t mut1 = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t mut2 = PTHREAD_MUTEX_INITIALIZER;

/* Потокова функція 1. */
void* thread_function1(void* unused)
{
  int i;
  /* Виведення на екран символу 'а' n разів. */
  printf("\n");
  for (i = 0; i < n; i++)
  {
    printf("a ");
  }
  printf("\n");
  
  printf("\nthread_function1 sends signal sig2 for the thread_function2\n");
  /* thread_function1 посилає сигнал sig2 для потоку thread_function2 */
  pthread_mutex_lock(&mut2);
  pthread_cond_signal(&sig2);
//  pthread_cond_broadcast(&sig2);
  pthread_mutex_unlock(&mut2);
  printf("\nSignal sig2 is sent!\n");

  printf("\nthread_function1 waits for receiving of the signal sig1\n");
  /* thread_function1 чекає на отримання сигналу sig1. */
  pthread_mutex_lock(&mut1);
  pthread_cond_wait(&sig1,&mut1);
  pthread_mutex_unlock(&mut1);

  printf("\nthread_function1 works after receiving of the signal sig1\n");
  /* Виведення на екран символу 'b' n разів. */
  printf("\n");
  for (i = 0; i < n; i++)
  {
    printf("b ");
  }
  printf("\n");
  return NULL;
}


/* Потокова функція 2. */
void* thread_function2(void* unused)
{
  int i;
  /* Виведення на екран символу '1' n разів. */
  printf("\n");
  for (i = 0; i < n; i++)
  {
    printf("1 ");
  }
  printf("\n");
  
  printf("\nthread_function2 sends signal sig1 for the thread_function1\n");
  /* thread_function2 посилає сигнал sig1 для потоку thread_function1 */
  pthread_mutex_lock(&mut1);
  pthread_cond_signal(&sig1);
//  pthread_cond_broadcast(&sig1);
  pthread_mutex_unlock(&mut1);
  printf("\nSignal sig1 is sent!\n");

  printf("\nthread_function2 waits for receiving of the signal sig2\n");
  /* thread_function2 чекає на отримання сигналу sig2. */
  pthread_mutex_lock(&mut2);
  pthread_cond_wait(&sig2,&mut2);
  pthread_mutex_unlock(&mut2);

  printf("\nthread_function2 works after receiving of the signal sig2\n");
  /* Виведення на екран символу '2' n разів. */
  printf("\n");
  for (i = 0; i < n; i++)
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

  /* Створюються потоки. */
  pthread_create (&thread1, NULL, &thread_function1, NULL);
  pthread_create (&thread2, NULL, &thread_function2, NULL);

  /* Очікується завершення потоків. */
  pthread_join (thread1, NULL);
  pthread_join (thread2, NULL);

  /*Завершення програми. */

  printf("\nProgram finished !!!\n");
  return 0;
}
