package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root =fxmlLoader.load();
        ((Controller)fxmlLoader.getController()).setStage(primaryStage);
        new DragListener(primaryStage).enableDrag(root);
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 1023, 701));
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.show();
    }

    static void setSliderValue(Label label,Slider slider, double value1,double value2){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                slider.setValue(value2);
                SimpleDateFormat format=new SimpleDateFormat("mm:ss");
                label.setText(format.format(value1*1000)+label.getText().substring(5));
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
