package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class MySlider extends StackPane {
    public MySlider() {
        //getStylesheets().add(this.getClass().getResource("style/Player.css").toExternalForm());
        Slider slider = new Slider();
        slider.setId("color-slider");
        Rectangle progressRec = new Rectangle();
        progressRec.heightProperty().bind(slider.heightProperty().subtract(7));
        progressRec.widthProperty().bind(slider.widthProperty());

        progressRec.setFill(Color.web("#c3c3c3"));

        progressRec.setArcHeight(15);
        progressRec.setArcWidth(15);

        slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
                // Using linear gradient we can fill two colors to show the progress
                // the new_val gets values between 0 - 100
                String style = String.format("-fx-fill: linear-gradient(to right, #1979ca %d%%, #c3c3c3 %d%%);",
                        new_val.intValue(), new_val.intValue());
                // set the Style
                progressRec.setStyle(style);
            }
        });

        getChildren().addAll(progressRec, slider);
    }
}

