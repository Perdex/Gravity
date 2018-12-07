package gravity.audio;

import java.io.File;
import java.net.URISyntaxException;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.*;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.*;
import javafx.util.Duration;


public class Audio extends JFXPanel{
    
    
    private static Media[] songs;
    private static final String[][] songNames = {{"Engel-Endless.mp3", "Kai Engel - Endless Story About Sun And Moon"},
                                            {"Cheremisinov-Forgotten.mp3", "Sergey Cheremisinov - Forgotten Stars"}};
    private static MediaPlayer musicPlayer;
    private static int songNum;
    private static boolean musicPlaying = true;
    
    public static MediaPlayer ROCKET_NOISE;
            
            
    public Audio(){
        ROCKET_NOISE = new MediaPlayer(toMedia("Noise.wav"));
        ROCKET_NOISE.setStartTime(Duration.millis(300));
        ROCKET_NOISE.setStopTime(Duration.millis(700));
        ROCKET_NOISE.setCycleCount(MediaPlayer.INDEFINITE);
        
        songs = new Media[songNames.length];
        
        songNum = (int)(Math.random() * songNames.length);
        
        for(int i = 0; i < songNames.length; i++){
            songs[i] = toMedia(songNames[i][0]);
        }
        
        startSong();
        
    }//Audio
    
    private Media toMedia(String s){
        try{
            return new Media(getClass().getResource(s).toURI().toString());
        }catch(URISyntaxException e){
            return null;
        }
    }//toMedia
    
    public static void toggleMusic(){
        
        if(musicPlaying)
            musicPlayer.stop();
        else{
            musicPlayer.play();
            gravity.Draw.songName = songNames[songNum][1];
        }
        
        musicPlaying = !musicPlaying;
        
    }//toggleMusic
    
    public void stop(){
        
        Timeline timeline = new Timeline();
        KeyFrame key = new KeyFrame(Duration.millis(1000), new KeyValue(musicPlayer.volumeProperty(), 0)); 
        timeline.getKeyFrames().add(key);   
        timeline.setOnFinished(new EventHandler(){
            @Override
            public void handle(Event e){
                musicPlayer.dispose();
                ROCKET_NOISE.dispose();
                Platform.exit();
            }
        });
        timeline.play();
        
        ROCKET_NOISE.dispose();
    }//stop
    
    
    
    private static void startSong(){
        
        
        musicPlayer = new MediaPlayer(songs[songNum]);
        
        try{
            Thread.sleep(1000);
        }catch(InterruptedException e){}
        
        
        gravity.Draw.songName = songNames[songNum][1];
        
        musicPlayer.play();
        
        musicPlayer.setOnStopped(new Runnable(){
            @Override
            public void run(){
                if(musicPlaying)
                    startSong();
            }
        });
        
        int newSong;
        do{
            newSong = (int)(Math.random() * songNames.length);
        }while(newSong == songNum);
        
        songNum = newSong;
        
    }//startSong
    
}
