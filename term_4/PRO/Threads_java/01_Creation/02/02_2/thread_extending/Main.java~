/* Стоворення двох потоків, використовуючи
 * спадкування класу Thread. Програма ілюструє використання
 * методу join та isAlive класу Thread
 */
class ChildThread extends Thread{ 
        
    private char chr; // Символ, для виводу на екран
    private int count; // лічильник, що вказує кількість виведених символів 
		    
    /* Конструктор класу ChildThread */
    ChildThread(char chr, int count){ 
        /*Виклик конструктора батьківського класу Thread 
	 * та створення нового потоку
	 */	
	super("Child Thread");
	/* Ініціалізація інформаційних полів */
	this.chr = chr;
	this.count = count;

	start(); // Запуск потокової функції
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
	    thread1.join();
	/* Чекаємо, доки завершиться другий потік  */
	    thread2.join();
	}catch(InterruptedException e){
	    System.out.println("Main thread interrupted");
	}
	
	/* Переконуємось, що завершився перший потік. */
	System.out.println("\nFirst thread is alive: " + thread1.isAlive());
	/* Переконуємось, що завершився другий потік. */
	System.out.println("Second thread is alive: " + thread2.isAlive());
    }

}




