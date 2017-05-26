#include <pthread.h>
#include <stdio.h>

/*
Програма побудована на основі програми з книги
М.Митчел, Д.Оулдем, А.Самьюэл. Програмирование для Linux. Профессиональный подход.: Пер. с англ. -
М.:Издательский дом "Вильямс", 2003. - 288с.:ил.
*/

/* Параметри для функції char_print(). */

struct char_print_parms
{
/* символ, що відображується. */ 
  char character;
/* скільки разів його треба вивести. */
  int count;
};

/* запис вказаного числа символів в потік stderr. Аргумент PARAMETERS є вказівником на структуру char_print_parms. */

void* char_print (void* parameters)
{
/* приведення вказівника до потрібного типу. */
  struct char_print_parms* p = (struct char_print_parms*) parameters;
  int i;

  for (i = 0; i < p->count; i++)
    fputc(p->character, stderr);
  return NULL;
}

/* Основна програма */

int main ()
{
  pthread_t thread1_id;
  pthread_t thread2_id;
  struct char_print_parms thread1_args;
  struct char_print_parms thread2_args;

/*Стрворення нового потоку, що відображує 30000 символів 'a'. */
  thread1_args.character = 'a';
  thread1_args.count = 30000;
  pthread_create (&thread1_id, NULL, &char_print, &thread1_args);

/*Стрворення нового потоку, що відображує 20000 символів 'b'. */
  thread2_args.character = 'b';
  thread2_args.count = 20000;
  pthread_create (&thread2_id, NULL, &char_print, &thread2_args);

  return 0;
}
