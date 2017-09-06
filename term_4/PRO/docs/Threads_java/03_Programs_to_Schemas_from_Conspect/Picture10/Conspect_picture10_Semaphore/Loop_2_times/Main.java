/* РІШЕННЯ ЗАВДАННЯ ПОВНОЇ СИНХРОНІЗАЦІЇ ДВОХ ПОТОКІВ
 * ЗА ДОПОМОГОЮ ДВОХ ДВІЙКОВИХ СЕМАФОРІВ
 */

/* Програма до рисунку 10 з конспекту */

//  Copyright (c) 2009 Oleksandr Marchenko. All rights reserved.

/* Бібліотеки, які потрібні для роботи з семафорами */
import java.util.concurrent.Semaphore;

class SharedSemaphore
{
	/* Ініціалізація семафорів */
	public static Semaphore thread_sem1 = new Semaphore(0, true);
	public static Semaphore thread_sem2 = new Semaphore(0, true);
}

class Thread_1 implements Runnable
{
	private final static int n = 10;
	/* Потоковий об'єкт */
	Thread t;

	/* конструктор */
	Thread_1()
	{
		/* Створення нового потоку */
		t = new Thread(this, "Thread_1");
		
		/* Запуск потокової функції */
		t.start();
	}

	/* Потокова функція(метод) */
	public void run()
	{
		for (int j = 0; j < 2; j++)
		{
		 int i;
 		 System.out.println("\nThread_1 works BEFORE the synchronization point.");
 		 /* Виведення на екран символу 'а' n разів. */
 		 for (i = 0; i<n; i++)
  		 {
   		 	System.out.print("a ");
  		 }
 		 System.out.println();
 		
 		  System.out.println("\nThread_1 opens semaphore thread_sem2 for the Thread_2");
 		  /* Функція відкриває семафор thread_sem2 для потоку Thread_2 */
 		  SharedSemaphore.thread_sem2.release();

 		  System.out.println("Semaphore thread_sem2 is opened!");

 		  System.out.println("Thread_1 waits for the opening of the semaphore thread_sem1");
 		  try
 		  {
 			 /* Функція чекає, поки потік Thread_2 відкриє семафор thread_sem1. */
 			  SharedSemaphore.thread_sem1.acquire();

		  }catch(InterruptedException e)
		  	{
			  	System.out.println("Thread_1 interrupted");
		  	}

 		  System.out.println("\nThread_1 works AFTER synchronization point.");
 		  /* Виведення на екран символу 'b' n разів. */
 		  for (i = 0; i < n; i++)
 		  {
 		    System.out.print("b ");
 		  }
 		  System.out.println();
		}
 	}
}



class Thread_2 implements Runnable
{
	private final static int m = 15;
	/* Потоковий об'єкт */
	Thread t;

	/* конструктор */
	Thread_2()
	{
		/* Створення нового потоку */
		t = new Thread(this, "Thread_1");
		/* Запуск потокової функції */
		t.start();
	}

	/* Потокова функція(метод) */
	public void run()
	{
		for (int j = 0; j < 2; j++)
		{
		  int i;
 		  System.out.println("\nThread_2 works BEFORE the synchronization point.");
		  /* Виведення на екран символу '1' m разів. */
		  for (i = 0; i < m; i++)
		  {
		    System.out.print("1 ");
		  }
 		  System.out.println();
		  
		  System.out.println("\nThread_2 opens semaphore thread_sem1 for the Thread_1");
		  /* Функція відкриває семафор thread_sem1 для потоку Thread_1 */
		  SharedSemaphore.thread_sem1.release();

		  System.out.println("Semaphore thread_sem1 is opened!");

		  System.out.println("Thread_2 waits for the opening of the semaphore thread_sem2");
 		  try
 		  {
 			  /* Функція чекає, поки потік Thread_1 відкриє семафор thread_sem2. */
 			  SharedSemaphore.thread_sem2.acquire();

		  }catch(InterruptedException e)
 		  	{
 				System.out.println("Thread_2 interrupted");
 		  	}

 		 System.out.println("\nThread_2 works AFTER semaphore thread_sem2");
		  /* Виведення на екран символу '2' m разів. */
		  for (i = 0; i < m; i++)
		  {
		    System.out.print("2 ");
		  }
		  System.out.println();
		}
	}
}

public class Main
{
	public static void main(String [] args)
	{
	 	  System.out.println("\nMain Started");
		
		  /* Створюються потоки. */
		  Thread_1 thread_1 = new Thread_1();
		  Thread_2 thread_2 = new Thread_2();

		  /* Очікується завершення потоків. */
		 try
		 {
			 /* Очікується завершення потоків. */
			 thread_1.t.join();
			 thread_2.t.join();
		 }catch(InterruptedException e)
		 	{
			 	System.out.println("\nMain Interrupted");
		 	}

		 System.out.println("\nMain Finished");
	}
	
}
