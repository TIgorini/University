/* ОТРИМАННЯ ІДЕНТИФІКАТОРІВ ПОТОЧНОГО ТА ДОЧІРНЬОГО ПОТОКІВ */

//  Copyright (c) 2009 Oleksandr Marchenko. All rights reserved.

class ChildThread_1 implements Runnable
{
	
	Thread t; 

	/* Конструктор класу ChildThread_1 */
	ChildThread_1()
	{
		
		/* створення нового потоку */
		t = new Thread(this, "ChildThread_1");
		t.start(); // запуск потокової функції
	}
	
	/* Потокова функція */
	public void run()
	{
		System.out.println("This is " + t.getName() + " its identifier is " + t.getId());
		while(true) 
			continue;
	} 
}

class ChildThread_2 implements Runnable
{
	
	Thread t; 

	/* Конструктор класу ChildThread_2 */
	ChildThread_2()
	{
		
		/* створення нового потоку */
		t = new Thread(this, "ChildThread_2");
		t.start(); // запуск потокової функції
	}
	
	/* Потокова функція */
	public void run()
	{
		System.out.println("This is " + t.getName() + " its identifier is " + t.getId());
		while(true) 
			continue;
	} 
}

public class Main
{
	
	/* Головний потік */
	public static void main(String [] args)
	{
		ChildThread_1	thread_1 = new ChildThread_1(); // Запускаємо потік 1
		ChildThread_2	thread_2 = new ChildThread_2(); // Запускаємо потік 2

		System.out.println("This is main. Its process ID is " + Thread.currentThread().getId());
		System.out.println("This is main. Identifier of the thread_1 is " + thread_1.t.getId());
		System.out.println("This is main. Identifier of the thread_2 is " + thread_2.t.getId());

		while(true)
			continue;
	}
}


