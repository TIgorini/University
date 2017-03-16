#include <pthread.h>
#include <stdio.h>

//  Copyright (c) 2009 Oleksandr Marchenko. All rights reserved.

/* Знаходимо суму чисел від 1 до number, де number - це значення, на яке вказує на параметр ARG. */
void* compute_sum (void* arg)
{
/* Приводимо тип аргументу до int */
	int n = *((int*) arg);

/* Вираховуємо суму */
	int s = 0;
	int i;

	for (i = 1; i <= n; i++)
		s = s + i;

/* Повертаємо знайдену суму до головної програми. */
        return (void*) s;
}

/* Головна програма. */
int main ()
{
	pthread_t thread;

	int number = 10;
	int sum;

/* Запускаємо потік, який обчислює потрібну суму
 * і передаємо йому число number для обчислення суми від 1 до number. */
	pthread_create (&thread, NULL, &compute_sum, &number);

/* Виконуємо інші дії. */

/* Чекаємо завершення потоку та приймаємо значення, яке він повертає, у змінну sum. */
	pthread_join (thread, (void*) &sum);

/* Виводимо обчислений результат. */
	printf ("Sum of numbers from 1 to %d is %d.\n", number, sum);

	return 0;
}

