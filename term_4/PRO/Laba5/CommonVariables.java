import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;

class CommonVariables {

    boolean bool_var;
    int int_var;
    long long_var;
    float float_var;
    double doub_var;

    ReentrantLock mutex = new ReentrantLock();
    Random random = new Random(2465);

    CommonVariables(){
        bool_var = false;
        int_var = 0;
        long_var = 0;
        float_var = 0.0f;
        doub_var = 0.0;
    }
}
