package sample;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.util.Duration;

public class Controller {
    @FXML private Line ready;
    @FXML private Line control;
    @FXML private Circle lamp;

    private int signal = 0;
    private Runnable startR = new Runnable() {
        @Override
        public void run() {
            signal = 1;
            ready.setStroke(Paint.valueOf("#cc0000"));
            try {
                Thread.sleep(1000);
                if (signal == 1) {
                    control.setStroke(Paint.valueOf("#cc0000"));
                    //Thread.sleep(1000);
                    lamp.setFill(Paint.valueOf("#EFF123"));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };
    private Runnable stopR = new Runnable() {
        @Override
        public void run() {
            signal = 0;
            ready.setStroke(Paint.valueOf("#000000"));
            try {
                Thread.sleep(1000);
                if (signal == 0) {
                    control.setStroke(Paint.valueOf("#000000"));
                    lamp.setFill(Paint.valueOf("#FFFFFF"));
                }
            } catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    };

    @FXML protected void startSignal(){
        Thread startThread = new Thread(startR);
        startThread.start();
    }

    @FXML protected void stopSignal(){
        Thread stopThread = new Thread(stopR);
        stopThread.start();
    }
}
