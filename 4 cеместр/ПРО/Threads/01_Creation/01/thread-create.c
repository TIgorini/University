#include <pthread.h>
#include <stdio.h>

/*
Програма побудована на основі програми з книги
М.Митчел, Д.Оулдем, А.Самьюэл. Програмирование для Linux. Профессиональный подход.: Пер. с англ. -
М.:Издательский дом "Вильямс", 2003. - 288с.:ил.
*/

/* Запис символів 'a' в потік stderr. Параметр не використовується. Функція ніколи не закінчується. */

void* print_a (void* unused)
{
  while (1)
    fputc('a', stderr);
  return NULL;
}

/* Основна програма */

int main ()
{
  pthread_t thread_id;
/* Створення потоку. Новий потік виконує функцію print_a(). */
  pthread_create(&thread_id, NULL, &print_a, NULL);
/* Неперервний запис символів 'm' в потік stderr. */
  while (1)
    fputc ('m', stderr);
  return 0;
}
