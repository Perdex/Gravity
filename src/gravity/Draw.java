package gravity;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.AffineTransformOp;
import java.awt.geom.AffineTransform;

public class Draw extends JPanel implements KeyListener{
    
    boolean rPr, lPr, ePr, qPr, dotPr, commaPr, i = false;
    long t, lastTime;
    int framerate = 50, timeCount;
    double time;
    Images image = new Images();
    Gravity main;
    JTextPane info = new JTextPane();
    SimpleAttributeSet atSet;
    String s = "controls: ship: A D W time: Q E " + 
                "camera: C zoom: < > reset: 1-5 exit: esc";
    
    public Draw(){
        main = new Gravity();
        addKeyListener(this);
        lastTime = System.currentTimeMillis();
        main.start();
        this.add(info);
        info.setBounds(getWidth()-120, 0, 120, 200);
        info.setForeground(Color.gray);
    }
    
    @Override
    public void paint(Graphics g){
        requestFocus();
        
        try{
        //clear
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());
        if(main.BGy-500+getHeight()/2 > 0){
            g.drawImage(image.BG, (int)main.BGx+getWidth()/2, 
                    (int)main.BGy-1500+getHeight()/2, null);
            g.drawImage(image.BG, (int)main.BGx-1000+getWidth()/2, 
                    (int)main.BGy-1500+getHeight()/2, null);
        }
        if(main.BGy-500 < getHeight()/2){
            g.drawImage(image.BG, (int)main.BGx+getWidth()/2, 
                    (int)main.BGy+500+getHeight()/2, null);
            g.drawImage(image.BG, (int)main.BGx-1000+getWidth()/2, 
                    (int)main.BGy+500+getHeight()/2, null);
        }
        g.drawImage(image.BG, (int)main.BGx+getWidth()/2, 
                (int)main.BGy-500+getHeight()/2, null);
        g.drawImage(image.BG, (int)main.BGx-1000+getWidth()/2, 
                (int)main.BGy-500+getHeight()/2, null);
        
        //draw info
        if(i){
            String s2 = "physics time: <" + main.FPS/10 + "ms draw time: <" + 
                    (int)time + "ms"; 
            info.setOpaque(false);
            info.setText(s2 + s);
            info.paint(g);
        }else{
            g.setColor(Color.gray);
            g.drawString("press I for info", getWidth()-80, 12);
        }
        
        //draw planet
        for(GravityObject go: main.GO){
            double temp = Math.sqrt(go.mass);
            g.drawImage(image.planet, (int)(main.zoom * (go.x-temp/2))+getWidth()/2, 
                    (int)(main.zoom * (go.y-temp/2))+getHeight()/2, 
                    (int)(main.zoom * temp), (int)(main.zoom * temp), null);
        }
        
        
        //draw prediction
        g.setColor(new Color(0, 125, 0));
        for(int i = 1; i < main.pred.length; i++){
            g.drawLine((int)(main.zoom * main.pred[i].getX())+getWidth()/2, 
                    (int)(main.zoom * main.pred[i].getY())+getHeight()/2, 
                    (int)(main.zoom * main.pred[i-1].getX())+getWidth()/2, 
                    (int)(main.zoom * main.pred[i-1].getY())+getHeight()/2);
        }
        
        
        //draw rocket
        double size = main.rocket.getSkin().getWidth();
        AffineTransform tx = AffineTransform.getRotateInstance(main.rocket.getRotRad(), 
                0.4 * main.zoom * size, 0.4 * main.zoom * size);
        tx.scale(0.8*main.zoom, 0.8*main.zoom);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        g.drawImage(op.filter(main.rocket.getSkin(), null),
                (int)(main.zoom * (main.pred[0].getX()-size/2))+getWidth()/2,
                (int)(main.zoom * (main.pred[0].getY()-size/2))+getHeight()/2, null);
        
        
        //keyIsPressed functions
        if(ePr){
            if(main.dToGo == main.dID)
                if(main.dToGo < 7.85)
                    main.dToGo += 0.15;
                else
                    main.dToGo = 8;
        }if(qPr){
            if(main.dToGo == main.dID)
                if(main.dToGo > 0.2)
                    main.dToGo -= 0.15;
                else
                    main.dToGo = 0.05;
            else
                main.dToGo = main.dID;
        }if(lPr){
            main.rocket.rot -= 6;
        }if(rPr){
            main.rocket.rot += 6;
        }if(dotPr){
            if(main.zoom < 1.5)
                main.zoom *= 1.05;
        }if(commaPr){
            if(main.zoom > 0.15)
                main.zoom *= 0.95;
        }
        
        //time indicator
        g.setColor(new Color(0, 125, 0));
        g.drawString("time speed", 5, 12);
        g.drawRect(5, 15, 72, 5);
        g.fillRect(5, 15, (int)(main.dID * 9), 5);
        
        //match framerate and wait
        t = System.currentTimeMillis() - lastTime;
        
        if(timeCount > 50){
            time -= time/2;
        }
        if(t > time){
            time = t;
            timeCount = 0;
        }else{
            timeCount++;
        }
        
        long toWait = 30 - t;
        if(toWait > 0)
        
            Thread.sleep(toWait);
        }catch(Exception e){
            System.out.println(e);
        }
        
        lastTime = System.currentTimeMillis();
        
        repaint();
    }
    
    
    @Override
    public void keyPressed(KeyEvent e){
        int key = e.getKeyCode();
        
        switch(key){
            case KeyEvent.VK_W:
                main.rocket.throttle = 1;
                break;
            case KeyEvent.VK_A:
                lPr = true;
                break;
            case KeyEvent.VK_D:
                rPr = true;
                break;
            case KeyEvent.VK_E:
                ePr = true;
                break;
            case KeyEvent.VK_Q:
                qPr = true;
                break;
            case KeyEvent.VK_R:
                main.reset = -1;
                break;
            case KeyEvent.VK_1:
                    main.reset = 1;
                break;
            case KeyEvent.VK_2:
                    main.reset = 2;
                break;
            case KeyEvent.VK_3:
                    main.reset = 3;
                break;
            case KeyEvent.VK_4:
                    main.reset = 4;
                break;
            case KeyEvent.VK_5:
                    main.reset = 5;
                break;
            case KeyEvent.VK_C:
                if(e.getKeyChar() == 'C'){
                    main.cameraMode--;
                    if(main.cameraMode == -2)
                        main.cameraMode = main.GONum-1;
                }else{
                    main.cameraMode ++;
                    if(main.cameraMode == main.GONum)
                        main.cameraMode = -1;
                }
                    
                break;
            case KeyEvent.VK_COMMA:
                commaPr = true;
                break;
            case KeyEvent.VK_PERIOD:
                dotPr = true;
                break;
            case KeyEvent.VK_I:
                i = !i;
                break;
            case KeyEvent.VK_ESCAPE:
                Gravity.fr.dispose();
                main.loop = false;
                break;
        }
        
    }
    
    @Override
    public void keyReleased(KeyEvent e){
        int key = e.getKeyCode();
        switch(key){
            case KeyEvent.VK_W:
                main.rocket.throttle = 0;
                break;
            case KeyEvent.VK_A:
                lPr = false;
                break;
            case KeyEvent.VK_D:
                rPr = false;
                break;
            case KeyEvent.VK_E:
                ePr = false;
                break;
            case KeyEvent.VK_Q:
                qPr = false;
                break;
            case KeyEvent.VK_COMMA:
                commaPr = false;
                break;
            case KeyEvent.VK_PERIOD:
                dotPr = false;
                break;
        }
        
    }
    
    @Override
    public void keyTyped(KeyEvent e){}
}
