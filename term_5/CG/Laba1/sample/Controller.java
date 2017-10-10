package sample;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class Controller {
    @FXML private Canvas canvas;
    @FXML private TextField xstart;
    @FXML private TextField ystart;
    @FXML private TextField xend;
    @FXML private TextField yend;
    @FXML private TextField xcenter;
    @FXML private TextField ycenter;
    @FXML private TextField radius;
    @FXML private Text timeDDA;
    @FXML private Text timeBres;
    @FXML private Text timeWu;
    @FXML private Text timeCirBres;

    private int x0, y0, x1, y1;
    private int maxWidth = 500;
    private int maxHeight = 400;

    @FXML protected void lineDDA(){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        PixelWriter pw = gc.getPixelWriter();
        gc.clearRect(0, 0, maxWidth, maxHeight);
        try {
            x0 = Math.round(Float.parseFloat(xstart.getText()));
            y0 = Math.round(Float.parseFloat(ystart.getText()));
            x1 = Math.round(Float.parseFloat(xend.getText()));
            y1 = Math.round(Float.parseFloat(yend.getText()));
        }catch (Exception e){
            x0 = 0; y0 = 0; x1 = 0; y1 = 0;
        }

        int L = Math.max(Math.abs(x1 - x0),Math.abs(y1 - y0));
        float dx = (float)(x1 - x0)/L;
        float dy = (float)(y1 - y0)/L;

        float x = x0;
        float y = y0;
        for (int i=0; i < L+1; i++){
            pw.setColor(Math.round(x), Math.round(y), Color.color(0, 0, 0, 1.0));
            x += dx;
            y += dy;
        }
    }

    @FXML protected void lineBres(){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        PixelWriter pw = gc.getPixelWriter();
        gc.clearRect(0, 0, maxWidth, maxHeight);
        try {
            x0 = Math.round(Float.parseFloat(xstart.getText()));
            y0 = Math.round(Float.parseFloat(ystart.getText()));
            x1 = Math.round(Float.parseFloat(xend.getText()));
            y1 = Math.round(Float.parseFloat(yend.getText()));
        }catch (Exception e){
            x0 = 0; y0 = 0; x1 = 0; y1 = 0;
        }

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

    private double fpart(double x){
        return x - Math.floor(x);
    }
    private void plot(PixelWriter pw, int x, int y, double c){
        pw.setColor(x, y, Color.color(0, 0, 0, c));
    }

    @FXML protected void lineWu(){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        PixelWriter pw = gc.getPixelWriter();
        gc.clearRect(0, 0, maxWidth, maxHeight);
        double x0, y0, x1, y1;
        try {
            x0 = Float.parseFloat(xstart.getText());
            y0 = Float.parseFloat(ystart.getText());
            x1 = Float.parseFloat(xend.getText());
            y1 = Float.parseFloat(yend.getText());
        }catch (Exception e){
            x0 = 0; y0 = 0; x1 = 0; y1 = 0;
        }

        boolean steep = Math.abs(y1 - y0) > Math.abs(x1 - x0);
        double buf;
        if (steep) {
            buf = x0;  x0 = y0;  y0 = buf;
            buf = x1;  x1 = y1;  y1 = buf;
        }
        if (x0 > x1){
            buf = x0;  x0 = x1;  x1 = buf;
            buf = y0;  y0 = y1;  y1 = buf;
        }

        double dx = x1 - x0;
        double dy = y1 - y0;
        double grad;
        if (dx == 0)
            grad = 1;
        else grad = dy/dx;

        int xend = (int)Math.round(x0);
        double yend = y0 + grad*(xend - x0);
        double xgap = 1.0 - fpart(x0 + 0.5);
        int xpxl1 = (int)Math.round(x0);
        int ypxl1 = (int)yend;
        if (steep){
            plot(pw, ypxl1, xpxl1, (1 - fpart(yend)) * xgap);
            plot(pw, ypxl1 + 1, xpxl1, fpart(yend) * xgap);
        }
        else{
            plot(pw, xpxl1, ypxl1, (1 - fpart(yend)) * xgap);
            plot(pw, xpxl1, ypxl1 + 1, fpart(yend) * xgap);
        }
        double intery = yend + grad;

        xend = (int)Math.round(x1);
        yend = y1 + grad*(xend - x1);
        xgap = 1.0 - fpart(x1 + 0.5);
        int xpxl2 = (int)Math.round(x1);
        int ypxl2 = (int)yend;
        if (steep){
            plot(pw, ypxl2, xpxl2, (1 - fpart(yend)) * xgap);
            plot(pw, ypxl2 + 1, xpxl2, fpart(yend) * xgap);
        }
        else{
            plot(pw, xpxl2, ypxl2, (1 - fpart(yend)) * xgap);
            plot(pw, xpxl2, ypxl2 + 1, fpart(yend) * xgap);
        }

        if (steep){
            for (int x = xpxl1 + 1; x <= xpxl2 - 1; x++){
                plot(pw, (int)intery, x, 1 - fpart(intery));
                plot(pw, (int)intery + 1, x, fpart(intery));
                intery += grad;
            }
        }else{
            for (int x = xpxl1 + 1; x <= xpxl2 - 1; x++){
                plot(pw, x, (int)intery, 1 - fpart(intery));
                plot(pw, x, (int)intery + 1, fpart(intery));
                intery += grad;
            }
        }
    }

    @FXML protected void cirBres(){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        PixelWriter pw = gc.getPixelWriter();
        gc.clearRect(0, 0, maxWidth, maxHeight);
        float r = 0;
        try {
            x0 = Math.round(Float.parseFloat(xcenter.getText()));
            y0 = Math.round(Float.parseFloat(ycenter.getText()));
            r = Float.parseFloat(radius.getText());
        }catch (Exception e){
            x0 = 0; y0 = 0;
        }

        int x = 0;
        int y = Math.round(r);
        int delta = 1 - (y << 1);
        int err;
        while (y > 0){
            pw.setColor(x0 + x, y0 + y, Color.color(0, 0, 0, 1.0));
            pw.setColor(x0 + x, y0 - y, Color.color(0, 0, 0, 1.0));
            pw.setColor(x0 - x, y0 + y, Color.color(0, 0, 0, 1.0));
            pw.setColor(x0 - x, y0 - y, Color.color(0, 0, 0, 1.0));
            err = ((delta + y) << 1) - 1;
            if ((delta < 0) && (err <= 0)){
                delta += ((++x) << 1) + 2;
                continue;
            }
            err = ((delta - x) << 1) - 1;
            if ((delta > 0) && (err > 0)){
                delta += 1 - ((--y) << 1);
                continue;
            }
            x++;
            delta += (x - y) << 1;
            y--;
        }

    }

    @FXML protected void cirNo(){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        PixelWriter pw = gc.getPixelWriter();
        gc.clearRect(0, 0, maxWidth, maxHeight);
        float r = 0;
        try {
            x0 = Math.round(Float.parseFloat(xcenter.getText()));
            y0 = Math.round(Float.parseFloat(ycenter.getText()));
            r = Math.round(Float.parseFloat(radius.getText()));
        }catch (Exception e){
            x0 = 0; y0 = 0;
        }


    }


    @FXML protected void timeTest(){
        System.out.println("Time for 2000 cycles:");
        long bgTime, endTime;

        bgTime = System.currentTimeMillis();
        for (int i = 0; i < 2000; i++)
            this.lineDDA();
        endTime = System.currentTimeMillis();
        timeDDA.setText((endTime - bgTime) + " ms");

        bgTime = System.currentTimeMillis();
        for (int i = 0; i < 2000; i++)
            this.lineBres();
        endTime = System.currentTimeMillis();
        timeBres.setText((endTime - bgTime) + " ms");

        bgTime = System.currentTimeMillis();
        for (int i = 0; i < 2000; i++)
            this.lineBres();
        endTime = System.currentTimeMillis();
        timeWu.setText((endTime - bgTime) + " ms");

        bgTime = System.currentTimeMillis();
        for (int i = 0; i < 2000; i++)
            this.cirBres();
        endTime = System.currentTimeMillis();
        timeCirBres.setText((endTime - bgTime) + " ms");

    }

    @FXML protected void clear(){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        PixelWriter pw = gc.getPixelWriter();
        gc.clearRect(0, 0, maxWidth, maxHeight);
        timeDDA.setText("");
        timeBres.setText("");
        timeWu.setText("");
        timeCirBres.setText("");
    }
}
