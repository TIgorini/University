/* СТВОРЕННЯ ПАРАЛЕЛЬНОГО ПОТОКУ ЗА ДОПОМОГОЮ ІНТЕРФЕЙСУ Runnable */

/* Клас для створення нового потоку
 * з використанням інтерфейсу Runnable
 */
class ChildThread implements Runnable{
	
    /* Екземпляр класу Thread, що містить усі потрібні 
    * методи для роботи з потоками  */
    Thread t; 
    
    /* Конструктор класу ChildThread */
    ChildThread(){ 
    /*Виклик конструктора класу Thread 
     * та створення нового потоку
     */
	t = new Thread(this, "Child Thread");
	System.out.println("Child Thread started");
	t.start(); // Запуск потокової функції
    }
		
    /* Потокова функція */
    public void run(){
	while(true)
	    System.err.print('a');
			
    }
}


public class Main{

    public static void main(String[] args) {
        System.out.println("Main Thread started");

        new ChildThread(); // Створення дочірнього потоку
        
	/* Неперервний вивід символа 'M' на екран. 
	 * Основний потік*/
        while(true)
	    System.err.print('M');
							    }
						    
}


