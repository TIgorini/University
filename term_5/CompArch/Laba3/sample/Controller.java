package sample;

import javafx.fxml.FXML;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class Controller {
    @FXML private Line ready;
    @FXML private Line control;
    @FXML private Line gate1;
    @FXML private Line gate2;
    @FXML private Line gate3;
    @FXML private Line out1;
    @FXML private Line out2;
    @FXML private Line out3;
    @FXML private Circle lamp;

    private int signal = 0;
    private Runnable loopRunnable = new Runnable() {
        @Override
        public void run() {
            boolean flag = false;
            while (true){
                System.out.print("");
                while (signal == 1) {
                    try {
                        Thread.sleep(1000);
                        out1.setStroke(Paint.valueOf("#cc0000"));
                        out2.setStroke(Paint.valueOf("#cc0000"));
                        out3.setStroke(Paint.valueOf("#cc0000"));
                        flag = !flag;
                        if (flag) {
                            control.setStroke(Paint.valueOf("#cc0000"));
                            lamp.setFill(Paint.valueOf("#EFF123"));
                        } else {
                            control.setStroke(Paint.valueOf("#000000"));
                            lamp.setFill(Paint.valueOf("#FFFFFF"));
                        }
                        Thread.sleep(1000);
                        out1.setStroke(Paint.valueOf("#000000"));
                        out2.setStroke(Paint.valueOf("#000000"));
                        out3.setStroke(Paint.valueOf("#000000"));

                    } catch (InterruptedException e) {
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
        gate1.setStroke(Paint.valueOf("#cc0000"));
        gate2.setStroke(Paint.valueOf("#cc0000"));
        gate3.setStroke(Paint.valueOf("#cc0000"));
        signal = 1;
    }

    @FXML protected void stopSignal(){
        ready.setStroke(Paint.valueOf("#000000"));
        gate1.setStroke(Paint.valueOf("#000000"));
        gate2.setStroke(Paint.valueOf("#000000"));
        gate3.setStroke(Paint.valueOf("#000000"));
        control.setStroke(Paint.valueOf("#000000"));
        signal = 0;
    }
}
