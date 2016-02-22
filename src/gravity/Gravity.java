package gravity;

import javax.swing.JFrame;
import javax.swing.JPanel;
public class Gravity extends Thread{
    
    public boolean loop = true;
    public double FPS = 0, dID = 1, dToGo = 2, BGx = 250, BGy = 250, zoom = 0.5;
    private long lastTime, t;
    public int reset = 0, FPSCount = 0, pMultiplier, GONum, cameraMode = -1;
    private final double slowDown = 1000;
    
    GravityObject[] GO;
    GravityObject[] pred;
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
        GONum = 1;
        reset();
    }
    
    private void reset(){
        if(GONum == 1){
            pred = new GravityObject[500];
            pMultiplier = 1;
        }else{
            pred = new GravityObject[100];
            pMultiplier = 3;
        }
        for(int i = 0; i < pred.length; i++){
            pred[i] = new GravityObject(i+1, i * i / 200-750, 50-i/20, -i^2/200, 0);
        }
        pred[0] = new GravityObject(1, -750, 10, 0, 0);
        rocket = new Rocket();
        BGx = 0;
        BGy = 0;
        dID = 4;
        dToGo = 4;
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
    public static void main(String[] args) {
        fr = new JFrame("Gravity");
        P = new Draw();
        
        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fr.setUndecorated(true);
        fr.setExtendedState(JFrame.MAXIMIZED_BOTH);
        fr.add(P);
        fr.setVisible(true);
        P.setFocusable(true);
    }
    
    @Override
    public void run(){
        while(loop){
            if(reset != 0){
                if(reset > 0)
                    GONum = reset;
                reset();
                reset = 0;
            }
            
            dID = dToGo;
            //go through gravity objects
            for(GravityObject g: GO){
                
                //slow down time when near objects
                double slow = slowDown * Math.sqrt((Math.pow(g.x-pred[0].x, 2) + 
                        Math.pow(g.y-pred[0].y, 2))) / g.mass;
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
            
            pred[0].xa += 0.1 * rocket.throttle * Math.sin(rocket.getRotRad());
            pred[0].ya -= 0.1 * rocket.throttle * Math.cos(rocket.getRotRad());
            
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
        
            pred[0].lastx = pred[0].x;
            pred[0].lasty = pred[0].y;

            pred[0].move(time);
            
            //'drop' predictions toward all gravity objects
            for(GravityObject g: GO){
                fall(pred[0], g, 1);
            }

        for(int i = 1; i < pred.length; i++){
            
            pred[i].lastx = pred[i-1].x;
            pred[i].lasty = pred[i-1].y;

            pred[i].x = pred[i-1].x + pMultiplier * time * pred[i-1].xs;
            pred[i].y = pred[i-1].y + pMultiplier * time * pred[i-1].ys;

            pred[i].x += pred[i-1].xa * time * time / 2;
            pred[i].y += pred[i-1].ya * time * time / 2;

            pred[i].xs = pred[i-1].xs + pred[i-1].xa * time;
            pred[i].ys = pred[i-1].ys + pred[i-1].ya * time;
            
            pred[i].xa = 0;
            pred[i].ya = 0;
            
            //'drop' predictions toward all gravity objects
            for(GravityObject g: GO){
                fall(pred[i], g, pMultiplier);
            }
        }
    }
    
    private void fall(GravityObject g, GravityObject g2, int multiplier){
        double dx = g2.x - g.x, dy = g2.y - g.y;
        double a = g2.mass / (Math.pow(dx, 2) + Math.pow(dy, 2));
        
        g.xa += multiplier * a * (double)(dx / (Math.abs(dx) + Math.abs(dy)));
        g.ya += multiplier * a * (double)(dy / (Math.abs(dx) + Math.abs(dy)));
        
    }
    
    private void center(){
        double toMovex = -pred[0].x;
        double toMovey = -pred[0].y;
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
