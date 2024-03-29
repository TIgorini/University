/* Стоворення двох потоків, використовуючи
 * інтерфейс Runnable. Програма ілюструє використання 
 * методів join та isAlive класу Thread
 */
class ChildThread implements Runnable{	
    /* Екземпляр класу Thread, що містить усі потрібні 
     * методи для роботи з потоками  */
    Thread t; 

    private char chr; // Символ, для виводу на екран
    private int count; // лічильник, що вказує кількість виведених символів 
	    
    /* Конструктор класу ChildThread */
    ChildThread(char chr, int count){ 
    /* Ініціалізація інформаційних полей */ 
	this.chr = chr;
	this.count = count;

    /*Виклик конструктора класу Thread 
     * та створення нового потоку
     */	
	t = new Thread(this, "Child Thread");
        t.start(); // Запуск потокової функції
    }
		    
    /* Потокова функція */
    public void run(){
	for(int i = 0; i < count; i++)
	    System.err.print(chr);
			    
    }
}

public class Main{
    public static void main(String [] args){
        /* Стрворення нового потоку, що відображує 30000 символів 'a'. */
	ChildThread thread1 = new ChildThread('a', 30000);
	
	/* Стрворення нового потоку, що відображує 20000 символів 'b'. */
	ChildThread thread2 = new ChildThread('b', 20000);
	
	// Використання методу join потребує конструкції обробки виключень
	try{
	/* Чекаємо, доки завершиться перший потік  */
	thread1.t.join();
	/* Чекаємо, доки завершиться другий потік  */
	thread2.t.join();
	}catch(InterruptedException e){
	    System.out.println("Main thread interrupted");
	}
	
	/* Переконуємось, що завершився перший потік. */
	System.out.println("\nFirst thread is alive: " + thread1.t.isAlive());
	/* Переконуємось, що завершився другий потік. */
	System.out.println("Second thread is alive: " + thread2.t.isAlive());
	
    }

}


