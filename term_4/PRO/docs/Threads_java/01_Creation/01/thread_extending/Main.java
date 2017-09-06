/* СТВОРЕННЯ ПАРАЛЕЛЬНОГО ПОТОКУ
 * ЗА ДОПОМОГОЮ СПАДКУВАННЯ КЛАСУ Thread 
 */

class ChildThread extends Thread{

    /* Конструктор класу ChildThread */
    ChildThread(){
	/* Виклик конструктора батьківського класу
	 * та створення нового потоку */
	super("ChildThread");
	
	System.out.println("Child Thread started");
	
	start(); // Запуск потокової функції
    }

    /* Потокова функція (метод) */
    public void run(){
	while(true)
            System.err.print('a');
     
    }
    
}

public class Main {

    /* Головний потік */
    public static void main(String[] args) {
        
	System.out.println("Main Thread started");
	
	new ChildThread(); // Створення дочірнього потоку
	
	/* Неперервний вивід символа 'M' на екран.
	 * Основний потік*/
	while(true)
	    System.err.print('M');
   }

}



