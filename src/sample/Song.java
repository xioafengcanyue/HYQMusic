package sample;

import com.sun.net.httpserver.Headers;
import javafx.scene.image.Image;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.AbstractID3v2Frame;
import org.jaudiotagger.tag.id3.ID3v23Frame;
import org.jaudiotagger.tag.id3.framebody.FrameBodyAPIC;

import javax.swing.text.html.ImageView;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Song {

    private boolean isLover;
    private String name;
    private String singer;
    private String album;
    private String size;
    private int time;
    private Image image;
    public File file;

    public Song(File file) throws Exception{
        //MP3File mp3File=new MP3File(new File("K_DA _ Aluna _ Wolftyla _ Bekuh Boom _ 英雄联盟 - Drum Go Dum.mp3"));
        //System.out.println(mp3File.getAudioHeader());
        MP3File mp3File=new MP3File(file);
        name=mp3File.getID3v2Tag().frameMap.get("TIT2").toString();
        name=name.substring(6,name.length()-3);
        singer=mp3File.getID3v2Tag().frameMap.get("TPE1").toString();
        singer=singer.substring(6,singer.length()-3);
        album=mp3File.getID3v2Tag().frameMap.get("TALB").toString();
        album=album.substring(6,album.length()-3);
        time=mp3File.getAudioHeader().getTrackLength();
        double d=mp3File.getFile().length()/(1024.0*1024.0);
        size=String.format("%.2f", d)+"M";
        AbstractID3v2Frame frame=(AbstractID3v2Frame) mp3File.getID3v2Tag().getFrame("APIC") ;
        FrameBodyAPIC bodyAPIC=(FrameBodyAPIC)frame.getBody();
        byte[] imageData = bodyAPIC.getImageData();
        image =new Image(new ByteArrayInputStream(imageData));
        this.file=file;
    }
    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;//地址相等
        }
        if(obj == null){
            return false;//非空性：对于任意非空引用x，x.equals(null)应该返回false。
        }
        if(obj instanceof Song){
            Song other = (Song) obj;
            //需要比较的字段相等，则这两个对象相等
            if(this.file.equals(other.file)){
                return true;
            }
        }
        return false;
    }

    public boolean isLover() {
        return isLover;
    }

    public void setLover(boolean lover) {
        isLover = lover;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }


    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
    public static void main(String args[]) throws ReadOnlyFileException, CannotReadException, TagException, InvalidAudioFrameException, IOException {
        //Song song=new Song(new MP3File("K_DA _ Aluna _ Wolftyla _ Bekuh Boom _ 英雄联盟 - Drum Go Dum.mp3"));
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
