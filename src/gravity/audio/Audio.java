package gravity.audio;

import java.util.Timer;
import java.util.TimerTask;
import java.io.File;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.*;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.*;
import javafx.util.Duration;


public class Audio extends JFXPanel{
    
    
    private Media[] songs;
    private final String[][] songNames = {{"Engel-Endless.mp3", "Kai Engel - Endless Story About Sun And Moon"},
                                            {"Cheremisinov-Forgotten.mp3", "Sergey Cheremisinov - Forgotten Stars"}};
    private MediaPlayer mediaPlayer;
    private int songNum;
    
    public Audio(){
        songs = new Media[songNames.length];
        
        songNum = (int)(Math.random() * songNames.length);
        
        for(int i = 0; i < songNames.length; i++){
            songs[i] = new Media(new File("src/gravity/audio/" + songNames[i][0]).toURI().toString());
        }
        
        scheduleNext();
        
    }//Music
    
    public void stop(){
        
        Timeline timeline = new Timeline();
            KeyFrame key = new KeyFrame(Duration.millis(1000), new KeyValue (mediaPlayer.volumeProperty(), 0)); 
            timeline.getKeyFrames().add(key);   
            timeline.setOnFinished(new EventHandler(){
                @Override
                public void handle(Event e){
                    mediaPlayer.dispose();
                    Platform.exit();
                }
            });
        timeline.play();
            
    }//stop
    
    private void scheduleNext(){
        new Timer().schedule(new TimerTask(){
            @Override
            public void run(){
                startSong();
            }
        }, 2000);
    }//scheduleNext
    
    
    private void startSong(){
        
        gravity.Draw.songName = songNames[songNum][1];
        
        mediaPlayer = new MediaPlayer(songs[songNum]);
        mediaPlayer.play();
        
        mediaPlayer.setOnStopped(new Runnable(){
            @Override
            public void run(){
                scheduleNext();
            }
        });
        
        int newSong;
        do{
            newSong = (int)(Math.random() * songNames.length);
        }while(newSong == songNum);
        
        songNum = newSong;
        
    }//startSong
    
}
