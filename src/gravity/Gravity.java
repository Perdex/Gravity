package gravity;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.util.ArrayList;

public class Gravity extends Thread{
    
    public boolean loop = true;
    public double FPS = 0, dID, dToGo, BGx = 250, BGy = 250, zoom;
    private long lastTime, t;
    public int reset = 0, FPSCount = 0, cameraMode = -1;
    
    final int predictionLength = 2000, slowDown = 1000, GONum = 5;
    
    private final double rocketThrottle = 0.02;
    
    GravityObject[] GO;
    ArrayList<GravityObject> pred = new ArrayList();
    Rocket rocket;
    
    public double getDTime(){
        if(dID > 0)
            return 0.01 * Math.pow(2, dID);
        else if(dID < 0)
            return -0.01 * Math.pow(2, -dID);
        else
            return 0;
    }
    
    public Gravity(){
        lastTime = System.nanoTime();
        reset();
    }
    
    private void reset(){
        for(int i = 0; i < predictionLength; i++){
            pred.add(new GravityObject());
        }
        pred.set(0, new GravityObject(1, -750, 10, 0, 0));
        
        rocket = new Rocket();
        BGx = 0;
        BGy = 0;
        dID = 4;
        dToGo = 4;
        zoom = 0.5;
        rocket.rot = 90;
        
        GO = new GravityObject[GONum];
        int d = 4000, s = 4;
        GO[0] = new GravityObject(0, 0, 0, 0, 100000);
        if(GONum > 1)
            GO[1] = new GravityObject(d, 0, 0, s, 10000);
        if(GONum > 2)
            GO[2] = new GravityObject(-d, 0, 0, -s, 10000);
        if(GONum > 3)
            GO[3] = new GravityObject(0, d, -s, 0, 10000);
        if(GONum > 4)
            GO[4] = new GravityObject(0, -d, s, 0, 10000);
    }
    
    static JPanel P;
    static JFrame fr;
    static gravity.audio.Audio audio;
    public static void main(String[] args) {
        fr = new JFrame("Gravity");
        Gravity g = new Gravity();
        P = new Draw(g);
        g.start();
        
        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fr.setUndecorated(true);
        fr.setExtendedState(JFrame.MAXIMIZED_BOTH);
        fr.add(P);
        fr.setVisible(true);
        P.setFocusable(true);
        
        audio = new gravity.audio.Audio();
        fr.add(audio);
        
    }
    
    @Override
    public void run(){
        while(loop){
            if(reset != 0){
                reset();
                reset = 0;
            }
            
            dID = dToGo;
            //go through gravity objects
            for(GravityObject g: GO){
                
                //slow down time when near objects
                double slow = slowDown * pred.get(0).getD(g) / g.mass;
                
                if(dID > 0)
                    while(dID > slow)
                        dID -= 0.5;
                else
                    while(dID < slow)
                        dID += 0.5;
            }
            
            //move GOs
            for(GravityObject g: GO){
                if(g != GO[0]){
                    fall(g, GO[0], 1);
                    g.move(getDTime());
                }
            }
            
            pred.get(0).ax += rocketThrottle * rocket.throttle * Math.sin(rocket.getRotRad());
            pred.get(0).ay -= rocketThrottle * rocket.throttle * Math.cos(rocket.getRotRad());
            
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
        for(GravityObject g: GO){
            fall(pred.get(0), g, 1);
        }
        
        pred.get(0).move(time);

        double predTime = time;
//        for(GravityObject g: GO){
//            double d = pred.get(0).getD(g);
//            predTime = Math.min(predTime, d * d / g.mass / 30);
//        }
        
        for(int i = 1; i < pred.size(); i++){
            
            GravityObject thisOne = pred.get(i);
            GravityObject lastOne = pred.get(i-1);
            
            thisOne.ax = lastOne.ax;
            thisOne.ay = lastOne.ay;
            
            thisOne.vx = lastOne.vx;
            thisOne.vy = lastOne.vy;
            
            thisOne.x = lastOne.x;
            thisOne.y = lastOne.y;
            
            //'drop' predictions toward all gravity objects
            for(GravityObject go: GO){
                fall(pred.get(i), go, predTime);
            }
            
            thisOne.move(predTime);
        }
    }
    
    private void fall(GravityObject g, GravityObject g2, double multiplier){
        double dx = g2.x - g.x, dy = g2.y - g.y;
        double a = g2.mass / (Math.pow(dx, 2) + Math.pow(dy, 2));
        
        g.ax += multiplier * a * (double)(dx / (Math.abs(dx) + Math.abs(dy)));
        g.ay += multiplier * a * (double)(dy / (Math.abs(dx) + Math.abs(dy)));
        
    }
    
    private void center(){
        double toMovex = -pred.get(0).x;
        double toMovey = -pred.get(0).y;
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
        BGx += (double)(toMovex / 20);
        BGy += (double)(toMovey / 20);
    }
    
}
