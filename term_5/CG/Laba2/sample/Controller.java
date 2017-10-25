package sample;

import javafx.fxml.FXML;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;

import java.util.ArrayList;

public class Controller {
    @FXML private Canvas canvas;
    @FXML private Pane pane;

    private int maxWidth = 550;
    private int maxHeight = 450;
    private ArrayList<ArrayList<Double>> dots = new ArrayList<>();
    private ArrayList<Circle> circles = new ArrayList<>();

    @FXML protected void putDot(MouseEvent e){
        ArrayList<Double> dot = new ArrayList<>();
        double x = e.getX();
        double y = e.getY();
        dot.add(x);
        dot.add(y);
        dots.add(dot);
        circles.add(new Circle(x+168, y+25, 2));
        pane.getChildren().add(circles.get(circles.size()-1));
    }

    private double fact(int n){
        double res = 1;
        for (int i = 1; i <= n; i++)
            res *= i;
        return res;
    }
    private double bx(double t, int n){
        double res = 0;
        for (int i = 0; i <= n; i++){
            res += (fact(n)/(fact(i)*fact(n-i))) * Math.pow(t, i) * Math.pow(1 - t, n - i) * dots.get(i).get(0);
        }
        return res;
    }
    private double by(double t, int n){
        double res = 0;
        for (int i = 0; i <= n; i++){
            res += (fact(n)/(fact(i)*fact(n-i))) * Math.pow(t, i) * Math.pow(1 - t, n - i) * dots.get(i).get(1);
        }
        return res;
    }

    @FXML protected void draw(){
        if (dots.size() < 2)
            return;
        GraphicsContext gc = canvas.getGraphicsContext2D();
        PixelWriter pw = gc.getPixelWriter();
        int n = dots.size() - 1;
        for (double t = 0; t <= 1; t += 0.0001){
            pw.setColor((int)Math.round(bx(t,n)), (int)Math.round(by(t,n)),
                    Color.valueOf("#000000"));
        }
    }

    @FXML private Slider cornering;
    @FXML protected void lineCornering(){
        ArrayList<ArrayList<Double>> temp = new ArrayList<>();
        copy(temp, dots);
        double fi = cornering.getValue() * Math.PI/180;
        for (ArrayList<Double> coord: dots){
            double oldx = coord.get(0);
            double oldy = coord.get(1);
            coord.set(0, oldx * Math.cos(fi) - oldy * Math.sin(fi));
            coord.set(1, oldx * Math.sin(fi) + oldy * Math.cos(fi));
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, maxWidth, maxHeight);
        for (Circle cir:circles) {
            pane.getChildren().remove(cir);
        }
        circles.clear();
        draw();
        dots.clear();
        copy(dots, temp);
    }

    @FXML private Slider scaling;
    @FXML protected void lineScaling(){
        double m = scaling.getValue();
        for (ArrayList<Double> coord: dots){
            coord.set(0, m * coord.get(0));
            coord.set(1, m * coord.get(1));
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, maxWidth, maxHeight);
        for (Circle cir:circles) {
            pane.getChildren().remove(cir);
        }
        circles.clear();
        draw();
    }

    @FXML private TextField dx;
    @FXML private TextField dy;
    @FXML protected void lineShift(){
        try {
            for (ArrayList<Double> coord : dots) {
                coord.set(0, coord.get(0) + Double.parseDouble(dx.getText()));
                coord.set(1, coord.get(1) + Double.parseDouble(dy.getText()));
            }
        } catch (Exception ignored){

        }
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, maxWidth, maxHeight);
        for (Circle cir:circles) {
            pane.getChildren().remove(cir);
        }
        circles.clear();
        draw();
    }

    @FXML protected void lineReflect(){
        for (ArrayList<Double> coord: dots){
            coord.set(0, 550-coord.get(0));
            coord.set(1, coord.get(1));
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, maxWidth, maxHeight);
        for (Circle cir:circles) {
            pane.getChildren().remove(cir);
        }
        circles.clear();
        draw();
    }

    @FXML protected void clear(){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, maxWidth, maxHeight);
        for (Circle cir:circles) {
            pane.getChildren().remove(cir);
        }
        circles.clear();
        dots.clear();
    }

    private void copy(ArrayList<ArrayList<Double>> a, ArrayList<ArrayList<Double>> b){
        for (ArrayList<Double> aB : b) {
            a.add(new ArrayList<>(aB));
        }
    }
}