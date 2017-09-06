/* РІШЕННЯ ЗАВДАННЯ ПОВНОЇ СИНХРОНІЗАЦІЇ ДВОХ ПОТОКІВ
 * ЗА ДОПОМОГОЮ ОДНОГО БАР'ЄРУ
 */

/* Програма до рисунку 10 з конспекту */

//  Copyright (c) 2009 Oleksandr Marchenko. All rights reserved.

/* Бібліотеки, які потрібні для роботи з бар'єрами */
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

class Global
{
	public static final int n = 10;
}

class Thread_1 implements Runnable
{
	// Потокова змінна
	Thread t;
	
	/* Оголошення Бар'єру */
	private CyclicBarrier br;
	
	/* Конструктор */
	Thread_1(CyclicBarrier brInit)
	{
		/* Створення нового потоку */
		t  = new Thread(this, "Thread_1");

		/* Ініціалізація поля br */
		br = brInit;

		/* Запуск потокової функції */
		t.start();
	}
	
	/* Потокова функція(Метод) */
	public void run()
	{
		for (int j = 0; j < 1; j++)
		{
		  System.out.println("\nThread_1 works BEFORE synchronization point");
		  for (int i = 0; i < Global.n; i++)
		  /* Виведення на екран символу 'а' n разів. */
		  {
		    System.out.print("a ");
		  }
		  System.out.println("\n");
		  
		  System.out.println("\nThread_1 waits until Thread_2 reaches synchronization point");
		  try
		  {
			  /* Потік 1 чекає, доки потік 2 дістанеться до точки синхронізації. */
			  br.await();
			  
		  }catch(BrokenBarrierException e)
		        {
			   System.out.println(e.getMessage());
		        }
		   catch(InterruptedException e)
		  	{
			  System.out.println(e.getMessage());
		  	}

		  System.out.println("\nThread_1 works AFTER synchronization point");
		  System.out.println();
		  for (int i = 0; i < Global.n; i++)
		  /* Виведення на екран символу 'b' n разів. */
		  {
		    System.out.print("b ");
		  }
		  System.out.println();
		}
	}
}

class Thread_2 implements Runnable
{
	// Потокова змінна
	Thread t;
	/* Оголошення Бар'єру */
	private CyclicBarrier br;
	
	/* Конструктор */
	Thread_2(CyclicBarrier brInit)
	{
		/* Створення нового потоку */
		t  = new Thread(this, "Thread_1");
		/* Ініціалізація бар'єру br*/
		br = brInit;
		/* Запуск потокової функції */
		t.start();
	}

	/* Потокова функція(Метод) */
	public void run()
	{
		for (int j = 0; j < 1; j++)
		{
		  System.out.println("\nThread_2 works BEFORE synchronization point");
		  for (int i = 0; i < Global.n; i++)
		  /* Виведення на екран символу '1' n разів. */
		  {
		    System.out.print("1 ");
		  }
		  System.out.println("\n");

		  System.out.println("\nThread_2  waits until Thread_1 reaches synchronization point");
		  try
		  {
			/* Потік 2 чекає, доки потік 1 дійде до точки синхронізації */  
			br.await();
			
		  }catch(InterruptedException e)
		  	{
			  System.out.println(e.getMessage());
		  	}
		   catch(BrokenBarrierException e)
		        {
			   System.out.println(e.getMessage());
		        }

		  System.out.println("\nThread_2 works AFTER synchronization point");
		  System.out.println();
		  for (int i = 0; i < Global.n; i++)
		  /* Виведення на екран символу '2' n разів. */
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
		  
		  /* Створення та ініціалізація бар'єру */
		  CyclicBarrier br = new CyclicBarrier(2);
		 
		  /* Створюються потоки. */
		  Thread_1 thread_1 = new Thread_1(br);
		  Thread_2 thread_2 = new Thread_2(br);

		 try
		 {
			 /* Очікується завершення потоків. */
			 thread_1.t.join();
			 thread_2.t.join();
		 }catch(InterruptedException e)
		 	{
			 	System.out.println(e.getMessage());
		 	}

		 System.out.println("\nMain finished"); 
	}
	
}
