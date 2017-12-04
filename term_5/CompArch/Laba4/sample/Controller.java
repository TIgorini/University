package sample;

import javafx.fxml.FXML;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class Controller {
    @FXML private Line ready;
    @FXML private Line control1;
    @FXML private Line control2;
    @FXML private Line control3;
    @FXML private Line control4;
    @FXML private Line control5;
    @FXML private Line gate01;
    @FXML private Line gate02;
    @FXML private Line gate03;
    @FXML private Line gate11;
    @FXML private Line gate12;
    @FXML private Line gate13;
    @FXML private Line out01;
    @FXML private Line out02;
    @FXML private Line out03;
    @FXML private Circle outp0;
    @FXML private Line out11;
    @FXML private Line out12;
    @FXML private Line out13;
    @FXML private Circle outp1;
    @FXML private Line neout0;
    @FXML private Circle lamp;

    private int signal = 0;
    private Runnable loopRunnable = new Runnable() {
        @Override
        public void run() {
            while (true){
                System.out.print("");
                while (signal == 1) {
                    try {
                        Thread.sleep(1000);
                        out01.setStroke(Paint.valueOf("#cc0000"));
                        out02.setStroke(Paint.valueOf("#cc0000"));
                        out03.setStroke(Paint.valueOf("#cc0000"));
                        neout0.setStroke(Paint.valueOf("#000000"));
                        control1.setStroke(Paint.valueOf("#000000"));
                        control2.setStroke(Paint.valueOf("#000000"));
                        control3.setStroke(Paint.valueOf("#000000"));
                        control4.setStroke(Paint.valueOf("#000000"));
                        control5.setStroke(Paint.valueOf("#000000"));
                        lamp.setFill(Paint.valueOf("#FFFFFF"));
                        Thread.sleep(1000);
                        out11.setStroke(Paint.valueOf("#cc0000"));
                        out12.setStroke(Paint.valueOf("#cc0000"));
                        out13.setStroke(Paint.valueOf("#cc0000"));
                        control1.setStroke(Paint.valueOf("#cc0000"));
                        control2.setStroke(Paint.valueOf("#cc0000"));
                        control3.setStroke(Paint.valueOf("#cc0000"));
                        control4.setStroke(Paint.valueOf("#cc0000"));
                        control5.setStroke(Paint.valueOf("#cc0000"));
                        lamp.setFill(Paint.valueOf("#EFF123"));
                        Thread.sleep(1000);
                        out01.setStroke(Paint.valueOf("#000000"));
                        out02.setStroke(Paint.valueOf("#000000"));
                        out03.setStroke(Paint.valueOf("#000000"));
                        neout0.setStroke(Paint.valueOf("#cc0000"));
                        Thread.sleep(1000);
                        out11.setStroke(Paint.valueOf("#000000"));
                        out12.setStroke(Paint.valueOf("#000000"));
                        out13.setStroke(Paint.valueOf("#000000"));

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
        gate01.setStroke(Paint.valueOf("#cc0000"));
        gate02.setStroke(Paint.valueOf("#cc0000"));
        gate03.setStroke(Paint.valueOf("#cc0000"));
        neout0.setStroke(Paint.valueOf("#cc0000"));
        ready.setStroke(Paint.valueOf("#cc0000"));
        gate11.setStroke(Paint.valueOf("#cc0000"));
        gate12.setStroke(Paint.valueOf("#cc0000"));
        gate13.setStroke(Paint.valueOf("#cc0000"));
        control1.setStroke(Paint.valueOf("#cc0000"));
        control2.setStroke(Paint.valueOf("#cc0000"));
        control3.setStroke(Paint.valueOf("#cc0000"));
        control4.setStroke(Paint.valueOf("#cc0000"));
        control5.setStroke(Paint.valueOf("#cc0000"));
        lamp.setFill(Paint.valueOf("#EFF123"));
        signal = 1;
    }

    @FXML protected void stopSignal(){
        ready.setStroke(Paint.valueOf("#000000"));
        gate01.setStroke(Paint.valueOf("#000000"));
        gate02.setStroke(Paint.valueOf("#000000"));
        gate03.setStroke(Paint.valueOf("#000000"));
        ready.setStroke(Paint.valueOf("#000000"));
        gate11.setStroke(Paint.valueOf("#000000"));
        gate12.setStroke(Paint.valueOf("#000000"));
        gate13.setStroke(Paint.valueOf("#000000"));
        signal = 0;
    }
}
