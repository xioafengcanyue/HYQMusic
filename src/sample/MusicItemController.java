package sample;

import com.sun.prism.shader.Solid_TextureSecondPassLCD_Loader;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.net.ConnectException;
import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;

public class MusicItemController  implements Initializable {
    public Button heart;
    public Label musicName;
    public Label singer;
    public Label album;
    public Label size;
    private Song song;
    private Controller controller;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        
    }
    public void setSongInfo(Song song){
        musicName.setText(song.getName());
        singer.setText(song.getSinger());
        album.setText(song.getAlbum());
        size.setText(song.getSize());
        this.song=song;
    }

    public void setLover(ActionEvent actionEvent) {
        System.out.println("MYLOVER");
    }
    public void setController(Controller controller){
        this.controller= controller;
    }
    public void setThisPlay(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount()==2){
            controller.PlayMusic(song.file);
        }
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }
}
