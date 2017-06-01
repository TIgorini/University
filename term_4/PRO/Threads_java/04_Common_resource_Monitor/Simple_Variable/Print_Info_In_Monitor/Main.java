// РІШЕННЯ ЗАДАЧІ PRODUCER-CONSUMER (СПІЛЬНИЙ РЕСУРС - ПРОСТА ЗМІННА)
// ЗА ДОПОМОГОЮ МОНІТОРА

//  Copyright (c) 2009 Oleksandr Marchenko. All rights reserved.

class CommonResource
{
	int n;
	boolean valueSet = false;

	synchronized int get()
	{
		while (!valueSet)
			try
			{
				wait(); 
			}
			catch (InterruptedException e)
			{
				System.out.println("InterruptedException");
			}

			System.out.println("Consumer received: " + n);
			
		valueSet = false;
		notify();
			
		return n;
	}
	
	synchronized void put (int arg)
	{
		while (valueSet)
		
			try
			{
				wait();
			}
			catch (InterruptedException e)
			{
				System.out.println("InterruptedException");
			}
			
		n = arg;
		System.out.println("Producer sended: " + n);
		
		valueSet = true;
		notify();
		
	}
}

class Producer implements Runnable
{
	CommonResource CR;
		
	Producer (CommonResource CR_arg)
	{
		CR = CR_arg;
		new Thread (this, "Producer").start();
	}
	
	public void run()
	{
		int j = 0;
		while (true) 
		{
			CR.put(++j);
		}
	}
}
	
class Consumer implements Runnable
{
	CommonResource CR;
		
	Consumer (CommonResource CR_arg)
	{
		CR = CR_arg;
		new Thread (this, "Consumer").start();
	}
	
	public void run()
	{
		int current_n;
		while (true) 
		{
			current_n = CR.get();
		}
	}
}

class Main
{
	public static void main (String args[])
	{
		System.out.println ("To stop running press Ctrl+C");

		CommonResource MyCR = new CommonResource();
		new Producer ( MyCR );
		new Consumer ( MyCR );
		
	}
}
