package gravity;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.util.ArrayList;

import gravity.audio.Audio;
import javafx.scene.media.MediaPlayer;

public class Gravity extends Thread{
    
    public boolean loop = true;
    public double FPS = 0, BGx, BGy, zoom;
    public int[] times = new int[]{1, 3, 10, 30, 100, 300, 1000, 5000};
    private long lastTime, t;
    public int reset = 0, FPSCount = 0, cameraMode = -1, dID;
    
    final int predictionLength = 8000, GONum = 3;
    
    private final double rocketThrottle = 0.005, fuelUsage = 0.001;
    
    
    GravityObject[] GO, GO2;
    ArrayList<GravityObject> pred = new ArrayList<>();
    Rocket rocket;
    
    public double getDTime(){
        return 0.1 * times[dID];
    }
    
    public Gravity(){
        lastTime = System.nanoTime();
        reset();
    }
    
    private void reset(){
        pred = new ArrayList<>();
        
        rocket = new Rocket(1, -1700, 7, 0, 0);
        pred.add(rocket);
        
        BGx = 0;
        BGy = 0;
        dID = 0;
        zoom = 0.5;
        rocket.rot = 90;
        rocket.fuel = 1;
        
        GO = new GravityObject[GONum];
        GO2 = new GravityObject[GONum];
        double d = 10000, s = 2.8;
        GO[0] = new GravityObject(0, 0, 0, 0, 100000);
        GO2[0] = new GravityObject(0, 0, 0, 0, 100000);
        if(GONum > 1){
            GO[1] = new GravityObject(d, 0, 0, s, 10000);
            GO2[1] = new GravityObject(d, 0, 0, s, 10000);
        }if(GONum > 2){
            GO[2] = new GravityObject(-d, 0, 0, -s, 10000);
            GO2[2] = new GravityObject(-d, 0, 0, -s, 10000);
        }if(GONum > 3){
            GO[3] = new GravityObject(0, d, -s, 0, 10000);
            GO2[3] = new GravityObject(0, d, -s, 0, 10000);
        }if(GONum > 4){
            GO[4] = new GravityObject(0, -d, s, 0, 10000);
            GO2[4] = new GravityObject(0, -d, s, 0, 10000);
        }
    }
    
    static JPanel P;
    static JFrame fr;
    static Audio audio;
    public static void main(String[] args){
        fr = new JFrame("Gravity");
        Gravity g = new Gravity();
        audio = new Audio();
        P = new Draw(g);
        g.start();
        
        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fr.setUndecorated(true);
        fr.setExtendedState(JFrame.MAXIMIZED_BOTH);
        fr.add(audio);
        fr.add(P);
        fr.setVisible(true);
        P.setFocusable(true);
        
        
    }
    
    @Override
    public void run(){
        while(loop){
            if(reset != 0){
                reset();
                reset = 0;
            }
            
            //go through gravity objects
            for(GravityObject g: GO){
                
                //slow down time when near objects
                double slow = 200 * pred.get(0).getD(g) / g.mass;
                
                while(dID > slow && dID > 0)
                    dID--;
                
            }
            
            //move GOs
            for(GravityObject g: GO){
                if(g != GO[0]){
                    fall(g, GO[0]);
                    g.move(getDTime());
                }
            }
            
            if(dID < 2 && rocket.fuel > 0 && rocket.throttle > 0){
                
                rocket.thrusting = true;
                
                pred.get(0).ax += rocketThrottle * Math.sin(rocket.getRotRad());
                pred.get(0).ay -= rocketThrottle * Math.cos(rocket.getRotRad());
                rocket.fuel -= getDTime() * fuelUsage;
                if(rocket.fuel < 0)
                    rocket.fuel = 0;
                
                
                if(!Audio.ROCKET_NOISE.getStatus().equals(MediaPlayer.Status.PLAYING))
                    Audio.ROCKET_NOISE.play();
            }else{
                rocket.thrusting = false;
                if(Audio.ROCKET_NOISE.getStatus().equals(MediaPlayer.Status.PLAYING))
                    Audio.ROCKET_NOISE.stop();
            }
            
            center();
            
            predict();
            
            //time
            t = System.nanoTime() - lastTime;
            
            if(FPSCount > 50){
                FPS -= (int)FPS/2;
            }
            if(t/100000 > FPS){
                FPS = t/100000;
                FPSCount = 0;
            }else{
                FPSCount++;
            }
            
            long toWait = 10000000 - t;
            if(toWait > 0)
                try{
                    Thread.sleep(toWait/1000000);
                }catch(InterruptedException e){}
            lastTime = System.nanoTime();
        }
    }
    
    
    public void predict(){
        double time = getDTime();

        //'drop' predictions toward all gravity objects
        for(GravityObject go: GO){
            fall(rocket, go);
        }
        
        rocket.move(time);
        
        GravityObject.copy(GO, GO2);
        
        ArrayList<GravityObject> pred2 = new ArrayList();
        pred2.add(rocket);

        for(int i = 1; i < predictionLength / time; i++){
            
            pred2.add(new GravityObject(pred2.get(i-1), GO2, time));
//            double tt = pred.get(i).getD(GO[0])/5000;
//            tt = 0.1 * tt*tt*tt + tt*Math.sqrt(tt) - 0.015 * tt*tt*tt*tt;
//            tt *= 3.75;
            
            
            //'drop' predictions toward all gravity objects
            for(GravityObject go: GO2){
                fall(pred2.get(i), go);
            }
            
            pred2.get(i).move(time);
        }
        pred = pred2;
        
    }
    
    public static void fall(GravityObject g, GravityObject g2){
        double dx = g2.x - g.x, dy = g2.y - g.y;
        double a = g2.mass / (Math.pow(dx, 2) + Math.pow(dy, 2));
        
        g.ax += a * (double)(dx / (Math.abs(dx) + Math.abs(dy)));
        g.ay += a * (double)(dy / (Math.abs(dx) + Math.abs(dy)));
        
    }
    
    private void center(){
        double toMovex = -rocket.x;
        double toMovey = -rocket.y;
        if(cameraMode >= 0){
            toMovex = -GO[cameraMode].x;
            toMovey = -GO[cameraMode].y;
        }
        
        for(GravityObject o: GO){
            o.x += toMovex;
            o.y += toMovey;
        }
        for(GravityObject o: pred){
            o.x += toMovex;
            o.y += toMovey;
        }
        BGx += (double)(toMovex / 300);
        BGy += (double)(toMovey / 300);
    }
    
}
