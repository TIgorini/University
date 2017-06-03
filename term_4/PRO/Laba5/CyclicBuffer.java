class CyclicBuffer {

    private final int SIZE = 10;
    private int r_indx;
    private int w_indx;
    private int delta;
    private int[] buf;

    CyclicBuffer(){
        r_indx = 0;
        w_indx = 0;
        delta = 0;
        buf = new int[SIZE];
    }

    synchronized int put(){

        while (isFull()){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int elem;
        if (w_indx != 0)
            elem = buf[w_indx - 1] + 1;
        else
            elem = buf[SIZE - 1] + 1;

        buf[w_indx] = elem;
        w_indx = (w_indx + 1) % SIZE;
        delta++;
        notify();

        return elem;
    }

    synchronized int get(){

        while (isEmpty())
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        int elem = buf[r_indx];
        r_indx = (r_indx + 1) % SIZE;
        delta--;
        notify();

        return elem;
    }

    private boolean isFull(){
        return delta == SIZE;
    }

    private boolean isEmpty(){
        return delta == 0;
    }
}
