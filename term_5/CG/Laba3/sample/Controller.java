package sample;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

public class Controller {
    public ToggleGroup mod;
    public ToggleGroup algorithm;
    @FXML private Canvas canvas;
    @FXML private Pane pane;

    private int maxWidth = 550;
    private int maxHeight = 450;
    private ArrayList<Double> begDot = new ArrayList<>();
    private ArrayList<Double> endDot = new ArrayList<>();
    private Circle begCir = new Circle();
    private Circle endCir = new Circle();
    private HashMap<Integer, LinkedList<Integer>> dotList = new HashMap<>();
    private boolean clearFlag = false ;

    @FXML protected void click(MouseEvent e){
        if (e.getButton().toString().equals("SECONDARY")) {
            if (clearFlag)
                clear();
            else lock();
            clearFlag = !clearFlag;
        } else {
            double x = e.getX();
            double y = e.getY();
            if (mod.getSelectedToggle().toString().matches(".*'Line'"))
                drawLine(x, y);
            else {
                if (algorithm.getSelectedToggle().toString().matches(".*'With priming'"))
                    priming((int) x, (int) y);
                else if (algorithm.getSelectedToggle().toString().matches(".*'Line XOR processing'"))
                    xor();
                else listOfPoints();
            }
        }
    }

    private void drawLine(double x, double y){
        if (begDot.size() == 0){
            begDot.add(x);
            begDot.add(y);
            endDot.add(x);
            endDot.add(y);
            begCir.setCenterX(x+168);
            begCir.setCenterY(y+25);
            begCir.setRadius(2);
            pane.getChildren().add(begCir);
        } else {
            endDot.add(0, x);
            endDot.add(1, y);
            pane.getChildren().remove(endCir);
            endCir.setCenterX(x+168);
            endCir.setCenterY(y+25);
            endCir.setRadius(2);
            pane.getChildren().add(endCir);
            lineBres((int) Math.round(endDot.get(2)),
                    (int) Math.round(endDot.get(3)),
                    (int) Math.round(endDot.get(0)),
                    (int) Math.round(endDot.get(1)));
        }
    }

    private void priming(int x, int y){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        PixelWriter pw = gc.getPixelWriter();
        Image image = canvas.snapshot(null, null);
        PixelReader pr = image.getPixelReader();

        Stack<Point2D> stack = new Stack<>();
        stack.push(new Point2D(x, y));
        Point2D top;

        while (!stack.empty()) {
            top = stack.pop();
            if (pr.getColor((int)top.getX(), (int)top.getY()).equals(Color.color(1, 1, 1, 1))) {
                pw.setColor((int)top.getX(), (int)top.getY(), Color.color(0, 0, 0, 1));
                image = canvas.snapshot(null,null);
                pr = image.getPixelReader();
                if (pr.getColor((int)top.getX(), (int)top.getY() - 1).equals(Color.color(1, 1, 1, 1)))
                    stack.push(new Point2D(top.getX(), top.getY() - 1));
                if (pr.getColor((int)top.getX(), (int)top.getY() + 1).equals(Color.color(1, 1, 1, 1)))
                    stack.push(new Point2D(top.getX(), top.getY() + 1));
                if (pr.getColor((int)top.getX() - 1, (int)top.getY()).equals(Color.color(1, 1, 1, 1)))
                    stack.push(new Point2D(top.getX() - 1, top.getY()));
                if (pr.getColor((int)top.getX() + 1, (int)top.getY()).equals(Color.color(1, 1, 1, 1)))
                    stack.push(new Point2D(top.getX() + 1, top.getY()));
            }
        }
    }

    private void listOfPoints(){
        LinkedList<Integer> buf;
        for (int y = 0; y < maxHeight; y++){
            if (dotList.containsKey(y)){
                buf = dotList.get(y);
                int i = 0;
                while (i < buf.size() - 1) {
                    if (Math.abs(buf.get(i + 1) - buf.get(i)) <= 1)
                        buf.remove(i);
                    else i++;
                }
                if ((buf.size() > 1) && (Math.abs(buf.get(i) - buf.get(i - 1)) <= 1))
                    buf.remove(i);

                if (buf.size() % 2 == 0 ){
                    for (i = 0; i < buf.size(); i += 2)
                        lineBresClear(buf.get(i), y, buf.get(i + 1), y);
                } else if (buf.size() == 3 ){
                    lineBresClear(buf.get(0), y, buf.get(1), y);
                    lineBresClear(buf.get(1), y, buf.get(2), y);
                }
            }
        }

    }

    private void xor(){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        PixelWriter pw = gc.getPixelWriter();
        Image image = canvas.snapshot(null, null);
        PixelReader pr = image.getPixelReader();

        boolean f;
        ArrayList<Integer> dots = new ArrayList<>();
        for (int y = 1; y < maxHeight - 1; y++){
            f = false;
            for (int x = 0; x < maxWidth - 2; x++) {
                if ( pr.getColor(x, y).equals(Color.color(0, 0, 0, 1)) &&
                        !pr.getColor(x + 1, y).equals(Color.color(0, 0, 0, 1))) {
                    f = !f;
                    dots.add(x);
                }
                if (f)
                    pw.setColor(x, y, Color.color(0, 0, 0, 1));
            }
            if (dots.size() == 1)
                for (int x = dots.get(0) + 1; x < maxWidth - 1; x++)
                    pw.setColor(x, y, Color.color(1, 1, 1, 1));
            dots.clear();
        }
    }

    private void lock(){
        if (endDot.size() <= 2 && !mod.getSelectedToggle().toString().matches(".*'Line'"))
            return;
        pane.getChildren().remove(begCir);
        pane.getChildren().remove(endCir);
        lineBres((int) Math.round(begDot.get(0)),
                (int) Math.round(begDot.get(1)),
                (int) Math.round(endDot.get(0)),
                (int) Math.round(endDot.get(1)));
    }

    private void clear(){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, maxWidth, maxHeight);
        pane.getChildren().remove(begCir);
        pane.getChildren().remove(endCir);
        begDot.clear();
        endDot.clear();
        dotList.clear();
    }

    private void lineBres(int x0, int y0, int x1, int y1){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        PixelWriter pw = gc.getPixelWriter();

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int err = 0;
        int derr;
        boolean s = true;

        if (dx >= dy){
            derr = dy;
            int y = y0;
            int x = x0, xend = x1;

            if (x0 > x1){
                x = x1;
                xend = x0;
                y = y1;
                s = y0 > y1;
            }else if (y0 > y1)
                s = false;

            for (; x <= xend; x++){
                pw.setColor(x, y, Color.color(0, 0, 0, 1.0));
                if (dotList.containsKey(y))
                    dotList.get(y).add(x);
                else{
                    dotList.put(y, new LinkedList<>());
                    dotList.get(y).add(x);
                }
                err += derr;
                if ((err << 1) >= dx){
                    if (s)
                        y++;
                    else y--;
                    err -= dx;
                }
            }
        }else{
            derr = dx;
            int x = x0, y = y0;
            int yend = y1;

            if (y0 > y1){
                y = y1;
                yend = y0;
                x = x1;
                s = x0 > x1;
            }else if (x0 > x1)
                s = false;

            for (; y <= yend; y++){
                pw.setColor(x, y, Color.color(0, 0, 0, 1.0));
                if (dotList.containsKey(y))
                    dotList.get(y).add(x);
                else{
                    dotList.put(y, new LinkedList<>());
                    dotList.get(y).add(x);
                }
                err += derr;
                if ((err << 1) >= dy){
                    if (s)
                        x++;
                    else x--;
                    err -= dy;
                }
            }
        }
    }

    private void lineBresClear(int x0, int y0, int x1, int y1){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        PixelWriter pw = gc.getPixelWriter();

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int err = 0;
        int derr;
        boolean s = true;

        if (dx >= dy){
            derr = dy;
            int y = y0;
            int x = x0, xend = x1;

            if (x0 > x1){
                x = x1;
                xend = x0;
                y = y1;
                s = y0 > y1;
            }else if (y0 > y1)
                s = false;

            for (; x <= xend; x++){
                pw.setColor(x, y, Color.color(0, 0, 0, 1.0));
                err += derr;
                if ((err << 1) >= dx){
                    if (s)
                        y++;
                    else y--;
                    err -= dx;
                }
            }
        }else{
            derr = dx;
            int x = x0, y = y0;
            int yend = y1;

            if (y0 > y1){
                y = y1;
                yend = y0;
                x = x1;
                s = x0 > x1;
            }else if (x0 > x1)
                s = false;

            for (; y <= yend; y++){
                pw.setColor(x, y, Color.color(0, 0, 0, 1.0));
                err += derr;
                if ((err << 1) >= dy){
                    if (s)
                        x++;
                    else x--;
                    err -= dy;
                }
            }
        }
    }
}
