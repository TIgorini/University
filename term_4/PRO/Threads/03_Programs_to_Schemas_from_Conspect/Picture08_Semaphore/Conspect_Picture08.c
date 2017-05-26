#include <pthread.h>
#include <stdio.h>
#include <semaphore.h>

//  Copyright (c) 2009 Oleksandr Marchenko. All rights reserved.

// Виключення одночасного доступу до спільного ресурсу
// за допомогою двійкового семафору

/* Оголошення семафору. */
sem_t sem1;
int n = 0;
int s = 0;

/* Потокова функція, що рахує суму непарних чисел від 1 до заданого натурального n. */
void* thread_function1 (void* unused)
{
  int i = 1;

  while(1)
  {
    /* Закривається семафор. */
    sem_wait (&sem1);

    /* Використовується значення змінної s, що спільно використовується двома потоками. */
    s = s + i;

    /* Відкривається семафор. */
    sem_post (&sem1);

    printf("s=%d, i=%d\n", s, i);
    i = i + 2;

    if (i > n)
	break;
  }

  return NULL;
}

/* Потокова функція, що рахує суму парних чисел від 2 до заданого натурального n. */
void* thread_function2 (void* unused)
{
  int i = 2;

  while(1)
  {
    /* Закривається семафор. */
    sem_wait (&sem1);

    /* Використовується значення змінної s, що спільно використовується двома потоками. */
    s = s + i;

    /* Відкривається семафор. */
    sem_post (&sem1);

    printf("s=%d, i=%d\n", s, i);
    i = i + 2;

    if (i > n)
	break;
  }

  return NULL;
}

int main()
{
  /* Оголошуються потокові змінні. */
  pthread_t thread1;
  pthread_t thread2;

  /*Ініціалізується семафор. */
  sem_init (&sem1, 0, 1);

  printf("Enter n: ");
  scanf("%d", &n);

  /* Створюються потоки. */
  pthread_create (&thread1, NULL, &thread_function1, NULL);
  pthread_create (&thread2, NULL, &thread_function2, NULL);

  /* Очікується завершення потоків. */
  pthread_join (thread1, NULL);
  pthread_join (thread2, NULL);

  /*Завершення програми. */
  printf("Resulting Sum = %d;  n = %d\n", s, n);

  return 0;
}
