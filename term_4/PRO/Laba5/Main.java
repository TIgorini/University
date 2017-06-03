import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.CyclicBarrier;

class Semaphores
{
    static Semaphore sem1 = new Semaphore(0, true);
    static Semaphore sem2 = new Semaphore(0, true);
}


class Producer1 implements Runnable {

    Thread t;
    CyclicBuffer cr1;
    private CyclicBarrier cb1;

    Producer1(CyclicBuffer cr1_arg, CyclicBarrier cbInit){
        System.out.print("P1 start\n");
        cr1 = cr1_arg;
        cb1 = cbInit;
        t = new Thread(this, "P1");
        t.start();
    }

    public void run(){
        while (true) {
            System.out.print("\tP1 open sem2\n");
                Semaphores.sem2.release();
            try {
                System.out.print("\tP1 wait sem1\n");
                Semaphores.sem1.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("P1 put " + cr1.put() + " to CR1");

            System.out.print("\tP1 wait CB1\n");
            try {
                cb1.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
            System.out.print("\tP1 after CB1\n");
        }
    }
}

class Producer2 implements Runnable {

    Thread t;
    CyclicBuffer cr1;

    Producer2(CyclicBuffer cr1_arg){
        System.out.print("P2 start\n");
        cr1 = cr1_arg;
        t = new Thread(this, "P2");
        t.start();
    }

    public void run() {
        while (true) {
            System.out.print("\tP2 open sem1\n");
            Semaphores.sem1.release();
            try {
                System.out.print("\tP2 wait sem2\n");
                Semaphores.sem2.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("P2 put " + cr1.put() + " to CR1");
        }
    }
}

class Producer3 implements Runnable {

    Thread t;
    CyclicBuffer cr1;
    CommonVariables cr2;
    private CyclicBarrier cb2;

    Producer3(CyclicBuffer cr1_arg, CommonVariables cr2_arg, CyclicBarrier cbInit){
        System.out.print("P3 start\n");
        cr1 = cr1_arg;
        cr2 = cr2_arg;
        cb2 = cbInit;
        t = new Thread(this, "P3");
        t.start();
    }

    public void run(){

        while (true){
            cr2.mutex.lock();
            cr2.bool_var = cr2.random.nextBoolean();
            cr2.int_var = cr2.random.nextInt();
            cr2.long_var = cr2.random.nextLong();
            cr2.doub_var = cr2.random.nextDouble();
            System.out.println("P3 set in CR2:" +
                                "\nbool = " + cr2.bool_var +
                                "\nint = " + cr2.int_var +
                                "\nlong = " + cr2.long_var +
                                "\ndouble = " + cr2.doub_var);
            cr2.mutex.unlock();

            System.out.print("\tP3 wait CB2\n");
            try {
                cb2.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
            System.out.print("\tP3 after CB2\n");

            System.out.println("P3 put " + cr1.put() + " to CR1");
        }
    }
}

class Consumer1 implements Runnable {

    Thread t;
    CyclicBuffer cr1;
    CommonVariables cr2;
    private CyclicBarrier cb1;

    Consumer1(CyclicBuffer cr1_arg, CommonVariables cr2_arg, CyclicBarrier cbInit){
        System.out.print("C1 start\n");
        cr1 = cr1_arg;
        cr2 = cr2_arg;
        cb1 = cbInit;
        t = new Thread(this, "C1");
        t.start();
    }

    public void run(){

        while (true){
            System.out.println("C1 get " + cr1.get() + " from CR1");

            System.out.print("\tC1 wait CB1\n");
            try {
                cb1.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
            System.out.print("\tC1 after CB1\n");

            cr2.mutex.lock();
            cr2.float_var = cr2.random.nextFloat();
            cr2.int_var = cr2.random.nextInt();
            cr2.long_var = cr2.random.nextLong();
            cr2.bool_var = cr2.random.nextBoolean();
            System.out.println("C1 set in CR2:" +
                    "\nbool = " + cr2.bool_var +
                    "\nint = " + cr2.int_var +
                    "\nlong = " + cr2.long_var +
                    "\nfloat = " + cr2.float_var);
            cr2.mutex.unlock();
        }
    }
}

class Consumer2 implements Runnable {

    Thread t;
    CyclicBuffer cr1;
    CommonVariables cr2;
    private CyclicBarrier cb2;

    Consumer2(CyclicBuffer cr1_arg, CommonVariables cr2_arg, CyclicBarrier cbInit){
        System.out.print("C2 start\n");
        cr1 = cr1_arg;
        cr2 = cr2_arg;
        cb2 = cbInit;
        t = new Thread(this, "C2");
        t.start();
    }

    public void run(){
        while (true){
            cr2.mutex.lock();
            cr2.float_var = cr2.random.nextFloat();
            cr2.int_var = cr2.random.nextInt();
            cr2.doub_var = cr2.random.nextDouble();
            cr2.bool_var = cr2.random.nextBoolean();
            System.out.println("C2 set in CR2:" +
                    "\nbool = " + cr2.bool_var +
                    "\nint = " + cr2.int_var +
                    "\nfloat = " + cr2.float_var +
                    "\ndouble = " + cr2.doub_var);
            cr2.mutex.unlock();

            System.out.print("\tC2 wait CB2\n");
            try {
                cb2.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
            System.out.print("\tC2 after CB2\n");

            System.out.println("C2 get " + cr1.get() + " from CR1");
        }
    }
}


class Main {

    public static void main(String[] args){

        System.out.print("Main start\n");
        CyclicBuffer cr1 = new CyclicBuffer();
        CommonVariables cr2 = new CommonVariables();
        CyclicBarrier cb1 = new CyclicBarrier(2);
        CyclicBarrier cb2 = new CyclicBarrier(2);

        Producer1 p1 = new Producer1(cr1, cb1);
        Producer2 p2 = new Producer2(cr1);
        Producer3 p3 = new Producer3(cr1, cr2, cb2);
        Consumer1 c1 = new Consumer1(cr1, cr2, cb1);
        Consumer2 c2 = new Consumer2(cr1, cr2, cb2);

        try {
            p1.t.join();
            p2.t.join();
            p3.t.join();
            c1.t.join();
            c2.t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.print("Main stop\n");
    }
}
