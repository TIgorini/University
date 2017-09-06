#include <stdio.h>
#include <malloc.h>
#include <unistd.h>
#include <semaphore.h>
#include <pthread.h>

//  Copyright (c) 2009 Oleksandr Marchenko. All rights reserved.

/* Виключення одночасного доступу до черги за допомогою м'ютекса
   і контроль виходу за задані межі буфера (від 0 до 100) за допомогою
   багатозначного семафора з використанням неблокуючого чекання sem_trywait()
*/

/* Потокові змінні */
pthread_t thread1;
pthread_t thread2;
pthread_t thread3;
pthread_t thread4;

/* максимально допустима довжина черги, що використовується в якості буфера */
int max_queue_length = 100;

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

/* Багатозначний семафор для підрахунку кількості елементів у черзі */
sem_t sem_q;

/*------------------------------------------------------------------*/

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

/* Точно відомо, що черга не пуста. Отримуємо з голови черги її поточний елемент 
   для подальшої обробки без додаткових перевірок */
        p = beg_q;
/* Видаляємо цей елемент із черги */
        beg_q = beg_q->next;

	return p;
}


/*------------------------------------------------------------------*/

/* Потокова функція, яка додає елементи до черги - ПОСТАЧАЛЬНИК (PRODUCER) */
void* thread_producer(void* arg)
{
/* Змінна для зберігання номера потоку */
int num = *(int*)arg;

/* Змінна для зберігання поточного значення лічильника семафора */
int sem_value;

/* Встановлення дозволу відміняти цей потік у будь-який момент (асинхронно) */
//    pthread_setcanceltype (PTHREAD_CANCEL_ASYNCHRONOUS,NULL);

    while (1) {
        if (beg_q == NULL) 
    		break;

/* Додаємо новий елемент до черги тільки у випадку,
 * якщо масимально допустима довжина черги не перевищена */
	sem_getvalue(&sem_q,&sem_value);
	if (sem_value < max_queue_length) {

/* Захоплення м'ютекса черги для додавання нового елементу */
            pthread_mutex_lock (&mut_q);

            add_elem();

	    sem_getvalue(&sem_q,&sem_value);
	    printf("Producer thread%d: semaphore=%d; element %d CREATED; \n",
		   num,sem_value,end_q->number);

/* Звільнення м'ютекса черги */
            pthread_mutex_unlock (&mut_q);

/* Повідомляємо про те, що в черзі з'явилося нове завдання.
 * Якщо потоки заблоковані в очікуванні семафору, то один з
 * них буде розблоковано для обробки нового елемента */
            sem_post (&sem_q);

	}

/* Затримка потоку на задану кількість мікросекунд */
	usleep(1);
    }

/* Відміна всіх інших потоків, оскільки завдання вичерпались
 * і постачальник припинив роботу */
    pthread_cancel(thread2);
    pthread_cancel(thread3);
    pthread_cancel(thread4);

    printf("Producer thread%d  stopped !!!\n",num);

    return NULL;
}

/*------------------------------------------------------------------*/

/* Потокова функція, яка забирає (видаляє) елементи з черги - СПОЖИВАЧ (CONSUMER) */
/* Взяття та обробка елементів черги виконуються, якщо елементи в черзі ще не закінчилися */
void* thread_consumer (void* arg)
{
/* Змінна для зберігання номера потоку */
int num = *(int*)arg;

/* Оголошення вказівника curr_elem, який буде вказувати на поточний елемент з голови черги,
   який буде переданий до функції actions_for_elem() для виконання над ним потрібних дій */
struct t_elem* curr_elem=NULL;

/* Змінна для зберігання поточного значення лічильника семафора */
int sem_value;

/* Встановлення дозволу відміняти цей потік у будь-який момент (асинхронно) */
//	pthread_setcanceltype (PTHREAD_CANCEL_ASYNCHRONOUS,NULL);

        while (1) {

/* Чекаємо доки семафор буде готовим. Якщо його значення більше 0,
 * то це означає, що черга не пуста, виконуємо дії і зменшуємо лічильник на 1. 
*/

/* !!! Якщо черга пуста, то семафор є заблокованим і sem_trywait повертає значення -1 
 *     для того, щоб можна було проаналізуівати це значення і виконати тим часом іншу роботу 
*/
/* !!! */    if ( sem_trywait (&sem_q) == 0 ) {

/* Захоплення м'ютекса черги */
                pthread_mutex_lock (&mut_q);
/* Тепер можна безпечно працювати з чергою */

/* Беремо вказівник на поточний елемент з голови черги у змінну curr_elem */
		curr_elem = (struct t_elem*)get_elem();
		
		sem_getvalue(&sem_q,&sem_value);
	        printf("Consumer thread%d: semaphore=%d; element %d TAKEN; \n",
                        num,sem_value,curr_elem->number);

/* Звільнення м'ютекса черги, оскільки доступ до 
   спільного ресурсу (тобто черги) поки-що завершений 
*/
                pthread_mutex_unlock (&mut_q);
		
/* Виконуємо дії з обробки поточного елемента черги без додаткових перевірок,
   оскільки проходження семафору гарантує наявність у черзі хоч одного елемента */
                actions_for_elem (curr_elem);

/* Звільняємо память обробленого вже елемента (чистимо пам’ять) */
                free (curr_elem);

/* Затримка потоку на задану кількість мікросекунд */
//		usleep(1);
/* !!! */    }
/* !!! */    else {
/* !!! Доки семафор заблокований, потік виконує іншу корисну роботу */
/* !!! */	        printf("Consumer thread%d does some useful work\n",num);
/* !!! */    }

        }

        printf("Consumer thread%d  stopped !!!\n",num);

	return NULL;
}

/*------------------------------------------------------------------*/

int main()
{

/* Встановлюємо початкове значення лічильника семафору рівним нулю */
	sem_init (&sem_q, 0, 0);

	int sem_value;
	sem_getvalue(&sem_q,&sem_value);
        printf("semaphore=%d\n",sem_value);

        int length_at_start=10;
        int i;

/* Створення початкової черги із заданою кількостю елементів length_at_start 
   перед запуском потоків
*/
        for(i=0;i<length_at_start;i++) {
		add_elem();
		sem_post(&sem_q);
	}
        printf("Queue with elements from 0-th to %d-th has been created !!!\n",length_at_start-1);
	sem_getvalue(&sem_q,&sem_value);
        printf("semaphore=%d\n",sem_value);

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
