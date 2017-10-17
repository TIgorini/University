package sample;

import javafx.fxml.FXML;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;

public class Controller {
    @FXML private Line ready;
    @FXML private Line control;
    @FXML private Circle lamp;

    private int signal = 0;
    private Runnable loopRunnable = new Runnable() {
        @Override
        public void run() {
            while (true){
                while (signal == 1){
                    try {
                        control.setStroke(Paint.valueOf("#cc0000"));
                        lamp.setFill(Paint.valueOf("#EFF123"));
                        Thread.sleep(1000);
                        control.setStroke(Paint.valueOf("#000000"));
                        lamp.setFill(Paint.valueOf("#FFFFFF"));
                        Thread.sleep(1000);
                    } catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    };
    private Thread loopThread = new Thread(loopRunnable);

    @FXML protected void startSignal(){
        if (!loopThread.isAlive()){
            loopThread.start();
        }
        ready.setStroke(Paint.valueOf("#cc0000"));
        signal = 1;
    }

    @FXML protected void stopSignal(){
        ready.setStroke(Paint.valueOf("#000000"));
        signal = 0;
    }
}
