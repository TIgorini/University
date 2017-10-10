package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage window) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        window.setTitle("CGLaba1");
        window.setScene(new Scene(root, 800, 450));
        window.setResizable(false);
        window.show();

    }


    public static void main(String[] args) {
        launch(args);
    }


}
