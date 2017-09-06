#include <stdio.h>
#include <malloc.h>
#include <unistd.h>
#include <pthread.h>

//  Copyright (c) 2009 Oleksandr Marchenko. All rights reserved.

/* Виключення одночасного доступу до черги за допомогою м'ютекса
   без контролю виходу за задані межі буфера (буфер необмежений зверху)
*/

/* Потокові змінні */
pthread_t thread1;
pthread_t thread2;
pthread_t thread3;
pthread_t thread4;


struct t_elem {
	/* Посилання на наступний елемент черги */
	struct t_elem* next;
	/* інформаційні поля елементу черги */
	int number;
};

	/* Черга елементів */
struct t_elem* beg_q=NULL;
struct t_elem* end_q=NULL;

	/* М'ютекс для захисту черги */
pthread_mutex_t mut_q = PTHREAD_MUTEX_INITIALIZER;

/*-----------------------------------------------------------------*/

actions_for_elem(struct t_elem* current)
{
/* Виконуємо потрібні дії з обробки поточного елемента черги */
/* . . . . . . . . . . . . . . . . . . . . . . . . . . . . . */
}

/* Додавання нового елементу до черги */
void add_elem()
{
/* Створення нового елементу черги */
        struct t_elem*  p = (struct t_elem*)malloc(sizeof(struct t_elem));
	p->next = NULL; 

	if (beg_q == NULL) {
	    p->number = 0;
	    beg_q = p;
	}
	else {
	    p->number = end_q->number+1;
	    end_q->next = p;
	}
	end_q = p;
}


/* Взяття чергового елементу з голови черги для обробки */
void* get_elem()
{
	void* p=NULL;

/* Перевіряємо чи є елементи в черзі */
                if (beg_q == NULL)
                        p = NULL;
                else {
/* Отримуємо з голови черги її поточний елемент черги для подальшої обробки */
                        p = beg_q;
/* Видаляємо цей елемент із черги */
                        beg_q = beg_q->next;
                }
	return p;
}

/*-----------------------------------------------------------------*/

/* Потокова функція, яка додає елементи до черги - ПОСТАЧАЛЬНИК (PRODUCER) */
void* thread_producer(void* arg)
{
/* Змінна для зберігання номера потоку */
int num = *(int*)arg;

    while (1) {
/* Якщо черга пуста, то закінчуємо роботу цього потоку */
        if (beg_q == NULL) 
    		break;

/* Захоплення м'ютекса черги для додавання нового елементу */
        pthread_mutex_lock (&mut_q);

        add_elem();
        printf("Producer thread%d: element %d CREATED;\n",num,end_q->number);

/* Звільнення м'ютекса черги */
        pthread_mutex_unlock (&mut_q);


/* Затримка потоку на задану кількість мікросекунд */
//	usleep(1);
    }

/* Відміна всіх інших потоків, оскільки завдання вичерпались
 * і постачальник припинив роботу */
    pthread_cancel(thread2);
    pthread_cancel(thread3);
    pthread_cancel(thread4);

    printf("Producer thread%d  stopped !!!\n",num);

    return NULL;
}

/*-----------------------------------------------------------------*/

/* Потокова функція, яка забирає (видаляє) елементи з черги - СПОЖИВАЧ (CONSUMER) */
/* Взяття та обробка елементів черги виконуються доки елементи в черзі не закінчаться */
void* thread_consumer (void* arg)
{
/* Змінна для зберігання номера потоку */
int num = *(int*)arg;

/* Оголошення вказівника curr_elem, який буде вказувати на поточний елемент з голови черги,
   який буде переданий до функції actions_for_elem() для виконання над ним потрібних дій */
struct t_elem* curr_elem=NULL;

        while (1) {

/* Захоплення м'ютекса черги */
                pthread_mutex_lock (&mut_q);
/* Тепер можна безпечно працювати з чергою */

/* Беремо вказівник на поточний елемент з голови черги у змінну curr_elem */
		curr_elem = (struct t_elem*)get_elem();
		
/* Звільнення м'ютекса черги, оскільки доступ до 
   спільного ресурсу (тобто черги) поки-що завершений 
*/
                pthread_mutex_unlock (&mut_q);

/* Чи була черга порожньою, тобто чи вдалося взяти з неї черговий елемент? 
   Якщо так, закінчуємо роботу потоку 
*/
                if (curr_elem == NULL)
                        break;
		
/* Виконуємо дії з обробки поточного елемента черги */
                actions_for_elem (curr_elem);
	        printf("Consumer thread%d: actions for element %d DONE;\n",
                        num,curr_elem->number);

/* Звільняємо память обробленого вже елемента (чистимо пам’ять) */
                free (curr_elem);

/* Затримка потоку на задану кількість мікросекунд */
//		usleep(1);

        }

        printf("Consumer thread%d  stopped !!!\n",num);

        return NULL;
}

/*-----------------------------------------------------------------*/

int main()
{
        int length_at_start=10;
        int i;

/* Створення початкової черги із заданою кількостю елементів length_at_start 
   перед запуском потоків
*/
        for(i=0;i<length_at_start;i++) add_elem();
        printf("Queue with elements from 0-th to %d-th has been created !!!\n",length_at_start-1);

/* Оголошення змінних для нумерації потоків */
        int thread1_number=1;
        int thread2_number=2;
        int thread3_number=3;
        int thread4_number=4;

/* Кожному потоку передається вказівник на його номер, приведений до типу void*  */
        pthread_create (&thread1,NULL,&thread_producer,(void*)&thread1_number);
        pthread_create (&thread2,NULL,&thread_consumer,(void*)&thread2_number);
        pthread_create (&thread3,NULL,&thread_consumer,(void*)&thread3_number);
        pthread_create (&thread4,NULL,&thread_consumer,(void*)&thread4_number);

/* Очікуємо завершення всіх потоків */
        pthread_join(thread1,NULL);
        pthread_join(thread2,NULL);
        pthread_join(thread3,NULL);
        pthread_join(thread4,NULL);


        printf("All threads stopped !!!\n");

        return 0;
}
