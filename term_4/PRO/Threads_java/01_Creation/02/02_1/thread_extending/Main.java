/* Стоворення двох потоків, використовуючи
 * спадкування класу Thread. Програма ілюструє механізм 
 * передачі параметрів у потік
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
	new ChildThread('a', 30000);

    /* Стрворення нового потоку, що відображує 20000 символів 'b'. */
	new ChildThread('b', 20000);

    }


}


