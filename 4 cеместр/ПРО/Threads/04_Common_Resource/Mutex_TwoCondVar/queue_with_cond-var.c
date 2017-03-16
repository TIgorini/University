#include <stdio.h>
#include <malloc.h>
#include <unistd.h>
//#include <semaphore.h> - Не потрібно 
#include <pthread.h>

//  Copyright (c) 2009 Oleksandr Marchenko. All rights reserved.

/* Виключення одночасного доступу до черги 
   і контроль виходу за задані межі буфера (від 0 до 100) 
   за допомогою за допомогою одного м'ютекса та двох сигнальних (умовних) змінних
*/
/* Синхронізація та виключення одночасного доступу до спільного ресурсу (буфера) 
   в таких задачах, як "Постачальник-Споживач" (тобто де вимагається модель типу "монітора"),
   за допомогою сигнальних (умовних) змінних ОС Linux можуть бути реалізовані 
   практично ідентично до класичної реалізації на універсальній паралельній мові
   (дивись розділ "Сигнальні змінні" конспекту)
*/

/* Потокові змінні */
pthread_t thread1;
pthread_t thread2;
pthread_t thread3;
pthread_t thread4;

/***********************************************************************************************/
/* Опис структур даних для синхронізації доступу до спільного ресурсу (буфера у вигляді черги) */
/***********************************************************************************************/

/* Дві спільні змінні, що контролюють довжину черги, що використовується в якості буфера */
int max_queue_length = 100; /* максимально допустима довжина черги */
int curr_queue_length =  0; /* поточна довжина черги */

/* М'ютекс для контролю за чергою */
pthread_mutex_t 	mut_q = PTHREAD_MUTEX_INITIALIZER;

/* Дві сигнальні (умовні) змінні для сигналізування про непорожність та неповноту черги */
pthread_cond_t  	cond_not_empty = PTHREAD_COND_INITIALIZER;
pthread_cond_t  	cond_not_full  = PTHREAD_COND_INITIALIZER;

/* Функція is_full повертає 1, якщо черга є повною */
int is_full()
{
	return curr_queue_length >= max_queue_length;
}

/* Функція is_empty повертає 1, якщо черга є пустою */
int is_empty()
{
	return curr_queue_length <= 0;
}

/****************************************************************************/
/* Опис структур даних реалізації спільного буфера (в даному випадку черги) */
/****************************************************************************/

struct t_elem {
	/* Посилання на наступний елемент черги */
	struct t_elem* next;
	/* інформаційні поля елементу черги */
	int number;
};

	/* Черга елементів */
struct t_elem* beg_q=NULL;
struct t_elem* end_q=NULL;


/**********************************************************************/
/* Реалізація роботи з буфером у вигляді черги                        */
/**********************************************************************/

/* Функція обробки поточного елемента черги */
actions_for_elem(struct t_elem* current)
{
/* Виконуємо потрібні дії з обробки поточного елемента черги */
/* . . . . . . . . . . . . . . . . . . . . . . . . . . . . . */
}

/* Функція додавання нового елементу до черги */
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
struct t_elem* get_elem()
{
	struct t_elem* p=NULL;

/* Точно відомо, що черга не пуста. Отримуємо з голови черги її поточний елемент 
   для подальшої обробки без додаткових перевірок */
        p = beg_q;
/* Видаляємо цей елемент із черги */
        beg_q = beg_q->next;

	return p;
}

/****************************************************************************/
/* Потокова функція-постачальник (PRODUCER) для додавання елементу до черги */
/****************************************************************************/

void* thread_producer(void* arg)
{
/* Змінна для зберігання номера потоку */
int num = *(int*)arg;

/* Встановлення дозволу відміняти цей потік у будь-який момент (асинхронно) */
//    pthread_setcanceltype (PTHREAD_CANCEL_ASYNCHRONOUS,NULL);

    while (1) {
        if (beg_q == NULL) 
    		break;

/* Захоплення м'ютекса черги для додавання до неї нового завдання */
	pthread_mutex_lock (&mut_q);

/* Чекаємо доки поточний розмір черги дозволить додати ще одне завдання */
//	if (is_full()) {
	while (is_full()) {
		pthread_cond_wait (&cond_not_full, &mut_q);
	}

/* Додаємо новий елемент до черги тільки у випадку,
 * якщо масимально допустима довжина черги не перевищена */
        add_elem();
	curr_queue_length++;

	printf("Producer thread%d: element %d CREATED; current queue length = %d;\n",
		num,end_q->number,curr_queue_length);

/* Звільнення м'ютекса черги */
	pthread_mutex_unlock (&mut_q);

/* Повідомляємо про те, що в черзі з'явилося нове завдання.
 * Якщо потоки-споживачі заблоковані в очікуванні цього сигналу,
 * то один з них буде розблоковано для обробки нового завдання. */
	pthread_cond_broadcast (&cond_not_empty);

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

/********************************************************/
/* Потокова функція-споживач (CONSUMER).                */
/* Виконується доки елементи в черзі ще не закінчилися. */
/********************************************************/

void* thread_consumer (void* arg)
{
/* Змінна для зберігання номера потоку */
int num = *(int*)arg;

/* Оголошення вказівника curr_elem, який буде вказувати на поточний елемент з голови черги,
   який буде переданий до функції actions_for_elem() для виконання над ним потрібних дій */
struct t_elem* curr_elem=NULL;

/* Встановлення дозволу відміняти цей потік у будь-який момент (асинхронно) */
//	pthread_setcanceltype (PTHREAD_CANCEL_ASYNCHRONOUS,NULL);

        while (1) {

/* Захоплення м'ютекса черги для взяття з неї чергового завдання */
		pthread_mutex_lock (&mut_q);
/* Тепер можна безпечно працювати з чергою */

/* Чекаємо доки в черзі не з'явиться хоч одне завдання */
//		if (is_empty()) {
		while (is_empty()) {
			pthread_cond_wait (&cond_not_empty, &mut_q);
		}

/* Точно відомо, що черга не пуста.
   Беремо вказівник на поточний елемент з голови черги у змінну curr_elem */
		curr_elem = get_elem();
		curr_queue_length--;
		
		printf("Consumer thread%d: element %d TAKEN; current queue length = %d;\n",
			num,curr_elem->number,curr_queue_length);

/* Звільнення м'ютекса черги */
		pthread_mutex_unlock (&mut_q);

/* Виконуємо дії з обробки поточного елемента черги без додаткових перевірок,
   оскільки проходження сигнальної (умовної) змінної гарантує наявність у черзі 
   хоч одного елемента */
                actions_for_elem (curr_elem);

/* Повідомляємо про те, що до черги можна додати хоча б одне нове завдання.
 * Якщо потоки-постачальники заблоковані в очікуванні сигналу,
 * то один з них буде розблоковано для додавання нового завдання. */
		pthread_cond_broadcast (&cond_not_full);

/* Звільняємо память обробленого вже елемента (чистимо пам’ять) */
                free (curr_elem);

/* Затримка потоку на задану кількість мікросекунд */
//		usleep(1);
        }

        printf("Consumer thread%d  stopped !!!\n",num);

	return NULL;
}

/******************************************************************************/
/*                    Головна програма                                        */
/******************************************************************************/

int main()
{
        int length_at_start=10;
        int i;

/* Створення початкової черги із заданою кількостю елементів length_at_start 
   перед запуском потоків
*/
        for(i=0;i<length_at_start;i++) {
		add_elem();
    	        curr_queue_length++;
	}
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
//        pthread_join(thread2,NULL);
//        pthread_join(thread3,NULL);
//        pthread_join(thread4,NULL);


        printf("All threads stopped !!!\n");

        return 0;
}
