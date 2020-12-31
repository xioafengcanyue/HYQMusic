package sample;

import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;


public class Controller implements Initializable {

    public Button btn1;
    public Slider musicSlider;//音乐播放进度条
    public Pane pane;//主界面根容器
    public Label timeLabel;//显示当前播放时间
    public Button playBtn;//开始播放按钮
    public Button nextBtn;//下一曲按钮
    public Button preBtn;//上一曲按钮
    public Button listBtn;//
    public Button updownBtn;//选择本地文件载入按钮
    public ListView<HBox> musicList;//存放载入的音乐的列表
    public AnchorPane title;//标题栏
    public ImageView musicImg;//显示MP3专辑封面的缩略图
    public Label musicInfo;//进度条侧显示MP3信息
    public Pane lyricPane;//歌词显示的底层容器
    public ImageView lyricBackImg;//歌词现实的背景图
    public ImageView lyricImg;//歌词界面展示的专辑图片
    public Button backToMain;//从歌词界面返回主界面
    public Pane lrcPane;//放置歌词的容器
    public Label lrcAlbum;//歌词界面显示专辑名称
    public Label lrcSinger;//歌词界面显示歌手名称
    public Label lrcName;//歌词界面显示MP3名称
    public TextField searchLine;
    public Slider volumSlider;
    private FileChooser musicFC;//歌曲文件选择器
    private Media m;//存放歌曲资源
    private MediaPlayer mp;//控制歌曲资源的播放器
    private volatile double totalTime;//存放歌曲总时间
    private volatile double nowTime;//存放歌曲当前时间
    private volatile boolean hasTouchSlider;//判断进度条是否被触摸
    private Task<Void> task;//用于设置进度条随着时间移动的进程
    private Thread musicThread;//用于设置进度条随着时间移动的进程
    private volatile boolean hasStop;//用于判断当前歌曲是否暂停
    private boolean hasMusic;//判断是否有音乐文件加载
    private Stage stage;//主舞台
    private ObservableList<HBox> observableList;//存放音乐的列表信息，放入ListView展示
    private ArrayList<Song> songs;//存放歌曲信息
    public ArrayList<Label> lryLabel;//存放歌词的标签，放入列表中用于操纵
    private LrcReader lrcReader;//自定义类，用于读取lrc文件信息
    private MusicItemController itemController;//获取音乐列表中每个子项目的Controller
    private TranslateTransition showTransition;//打开歌词界面动画
    private TranslateTransition closeTransition;//关闭歌词界面动画




    class MusicTask extends Task {

        @Override
        protected Object call() throws Exception {
            do {
                while (!hasTouchSlider && hasStop) {
                    Thread.sleep(200);//线程暂停0.2s
                    if (nowTime < totalTime - 1)
                        nowTime += 0.2;//当前时间增加0.2s
                    Main.setSliderValue(timeLabel, musicSlider, nowTime, (nowTime / totalTime) * 100);
                }//设置进度条的新的值
            } while (!Thread.currentThread().isInterrupted());
            return null;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        hasMusic=false;
        hasStop=false;
        hasTouchSlider=false;
        nowTime=0;
        totalTime=0;
        songs=new ArrayList<>();
        lryLabel=new ArrayList<>();

        showTransition =new TranslateTransition(Duration.millis(300), lyricPane);
        showTransition.setFromY(0);
        showTransition.setToY(-721);
        closeTransition=new TranslateTransition(Duration.millis(500), lyricPane);
        closeTransition.setFromY(-721);
        closeTransition.setToY(0);




        musicFC = new FileChooser();
        musicFC.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("MP3", "*.mp3"),
                new FileChooser.ExtensionFilter("WAV", "*.wav")
        );//只能选择wav或mp3文件
        musicFC.setInitialDirectory(new File("./"));//设置文件打开路径为当前项目

        observableList= FXCollections.observableArrayList();
        musicList.setItems(observableList);
        setLrc();//设置歌词展示面板
        //musicList.setSelectionModel();
        //System.out.println(musicList.getItems().get(0).toString());




    }

    public void setMusicPro() {

        System.out.println(musicSlider.getValue());
        System.out.println(mp.getStatus());
    }

    public void openFile(ActionEvent actionEvent) {
        File musicFile = musicFC.showOpenDialog(stage);
        if(musicFile==null)//文件不为空则开始尝试播放音乐
            return;
        PlayMusic(musicFile);
    }

    void PlayMusic(File musicFile){
        if(mp!=null&&mp.getStatus()==MediaPlayer.Status.PLAYING){
            mp.dispose();//如果当前正在播放，播放器释放资源，重置当前播放时间
            nowTime=0;
        }
        if (musicFile != null) {
            m = new Media(musicFile.toURI().toString());
            mp = new MediaPlayer(m);//获取音乐文件资源，加载进播放器
            Song song = null;
            try {
                song=new Song(musicFile);//创建一个歌曲类，包含歌曲的各种信息
            } catch (Exception e) {
                e.printStackTrace();
            }
            lrcReader=new LrcReader(new File(musicFile.getPath().substring(0,musicFile.getPath().length()-3)+"lrc"));//该自定义类用于解析歌词
            for (Map.Entry<Long, String> entry : lrcReader.lrcMap.entrySet()) {//解析的歌词时间作为标记给播放器进行设置
                m.getMarkers().put(String.valueOf(entry.getKey()),Duration.millis(entry.getKey()));
            }
            for(int i=3;i<7;i++)//设置开始前四句的歌词
                lryLabel.get(i).setText(lrcReader.lrcList.get(i-3).substring(10));
            mp.setOnMarker(mediaMarkerEvent -> {//到达标记时间，滚动歌词，添加下一句歌词
                String lrc=lrcReader.getEachLrc(Long.valueOf(mediaMarkerEvent.getMarker().getKey()));
                lryLabel.get(0).setText(lrc);
                lryMove();//滚动歌词
            });
            lrcName.setText(song.getName());//设置音乐的信息
            lrcSinger.setText("歌手："+song.getSinger());
            lrcAlbum.setText("专辑："+song.getAlbum());
            Image image=song.getImage();//设置专辑图片的信息
            musicImg.setImage(image);
            lyricImg.setImage(image);
            lyricBackImg.setEffect(new GaussianBlur(40));//设置背景高斯模糊
            lyricBackImg.setPreserveRatio(false);//设置背景图片大小
            lyricBackImg.setImage(image);
            musicInfo.setText(song.getName()+"-"+song.getSinger());//设置歌曲信息
            if(!songs.contains(song)) {//如果没有添加，将给歌曲添加到列表进行显示
                songs.add(song);
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("music.fxml"));
                    HBox hBox = fxmlLoader.load();
                    itemController=fxmlLoader.getController();
                    itemController.setSongInfo(song);
                    itemController.setController(this);
                    observableList.add(hBox);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mp.setOnReady(() -> {//播放器完全载入音乐文件后，设置播放总时间
                hasMusic=true;
                totalTime=mp.getMedia().getDuration().toSeconds();
                SimpleDateFormat format=new SimpleDateFormat("mm:ss");
                StringBuilder allTime=new StringBuilder();
                allTime.append("00:00/");
                allTime.append(format.format(totalTime*1000));
                timeLabel.setText(allTime.toString());
                mp.volumeProperty().bind(volumSlider.valueProperty());
                mp.play();
                playBtn.setStyle("-fx-background-image:url(/imgsource/pause.png);" );
                hasStop=true;
                if(musicThread!=null)//如果新的歌曲载入，终止上个歌曲播放的线程
                    musicThread.interrupt();
                musicThread=new Thread(new MusicTask());//进度条移动线程开始执行
                musicThread.start();
            });
        }
    }


    public void setNewPlayTime(MouseEvent mouseEvent) {//进度条拖动触发的事件
        if(mp!=null) {
            if(nowTime!=musicSlider.getValue()* 0.01 * totalTime) {
                hasTouchSlider=false;
                nowTime = musicSlider.getValue() * 0.01 * totalTime;
                System.out.println(nowTime);
                mp.seek(Duration.seconds(nowTime));//寻炸根据进度条百分比对应的新的播放时间
            }
        }
    }

    public void setSliderTouch(MouseEvent mouseEvent) {//进度条被点击触发事件
        if(mp!=null)
            hasTouchSlider=true;//点击了进度条，设为true，进度条移动线程将会暂停
    }

    public void switchPlayStatus(ActionEvent actionEvent) {//暂停按钮点击触发事件
        if(!hasMusic)
            return;
        if(hasStop){
            playBtn.setStyle("-fx-background-image:url(/imgsource/play.png);" );
            hasStop=false;
            mp.pause();
        }else{
            System.out.println("ok");
            playBtn.setStyle("-fx-background-image:url(/imgsource/pause.png);" );
            hasStop=true;
            mp.play();
        }

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    public void closeStage(){
        stage.close();
        System.exit(0);
    }
    public void minStage(){
        stage.setIconified(true);
    }
    public void showLyric(MouseEvent mouseEvent) {
        if(songs.size()==0)
            return;
        showTransition.play();
    }
    public void closeLyric(ActionEvent actionEvent) {
        closeTransition.play();
    }
    public void setLrc(){//设置歌词背景的准备工作
        Pane lrcSubPane=new Pane();
        lrcSubPane.setStyle("-fx-background-color: transparent");
        for(int i=0;i<7;i++){//七个用于显示歌词内容的标签
            Label label=new Label();
            label.setStyle("-fx-font-size: 18px;\n" +
                    "    -fx-text-fill: white");
            label.setAlignment(Pos.CENTER);
            label.setPrefSize(450,40);
            lrcSubPane.getChildren().add(label);
            if(i!=3)
                label.setTranslateZ(50);//设置z轴，用于显示3D效果，放大正在播放的那句歌词
            else
                label.setTranslateZ(0);
            label.setTranslateY(40*i);
            lryLabel.add(label);
        }
        SubScene subScene=new SubScene(lrcSubPane,450,280, true,SceneAntialiasing.BALANCED);
        subScene.setCamera(new PerspectiveCamera());//设置3D相机
        lrcPane.getChildren().add(subScene);
    }
    public void lryMove(){//歌词滚动函数
        for(int i=0;i<7;i++)
            lrcEachMove(i);
        Label label=lryLabel.get(0);
        lryLabel.add(label);
        lryLabel.remove(0);

    }
    public void lrcEachMove(int order){//每个歌词滚动的动画设置
        TranslateTransition tt = new TranslateTransition(Duration.millis(1000), lryLabel.get(order));
        if(order==0) {
            tt.setFromY(280);
            tt.setToY(240);
            tt.play();
        }
        else {
            if(order==3){
                tt.setFromZ(0);
                tt.setToZ(50);
                lryLabel.get(order).setStyle("-fx-font-size: 18px;\n" +
                        "    -fx-text-fill: white");
            }
            if(order==4){
                tt.setFromZ(50);
                tt.setToZ(0);
                lryLabel.get(order).setStyle("-fx-font-size: 18px;\n" +
                        "    -fx-text-fill: #1ECC94");
            }
            tt.setFromY(order*40);
            tt.setToY((order-1) *40);
            tt.play();
        }
    }
}


