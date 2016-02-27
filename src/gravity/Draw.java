package gravity;

import java.awt.BorderLayout;
import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.AffineTransformOp;
import java.awt.geom.AffineTransform;
import java.awt.Font;

public class Draw extends JPanel{
    
    boolean rPr, lPr, dotPr, commaPr;
    private long t, lastTime;
    private int framerate = 50, timeCount;
    private double time;
    private final Images image = new Images();
    private final Gravity main;
    final JTextPane info = new JTextPane();
    private String s = "\nControls:"
                    + "\nship: A D W"
                    + "\ntime: Q E"
                    + "\ncamera: C"
                    + "\nzoom: < >"
                    + "\nreset: R"
                    + "\nmusic: M"
                    + "\nthis info: I"
                    + "\nexit: esc";
    
    public static String songName;
    private int songNamePhase = 0;
    
    public Draw(Gravity main){
        this.main = main;
        addKeyListener(new Actions(this, main));
        lastTime = System.currentTimeMillis();
        
        setOpaque(false);
        setLayout(new BorderLayout());
        
        info.setEditable(false);
        info.setFocusable(false);
        info.setForeground(Color.gray);
        info.setOpaque(false);
        add(info, BorderLayout.WEST);
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
        
        
        //update info
        String s2 = "physics time: " + (double)((int)main.FPS)/10 + "ms\ndraw time: " + 
                (int)time + "ms\n" + s + "\n\nacceleration: " + (double)((int)(main.pred.get(0).getA()*100))/100 +
                "\ndist from center: " + (int)main.pred.get(0).getD(main.GO[0]) + "\n"; 
        info.setText(s2);
        
        
        //draw planet
        for(GravityObject go: main.GO){
            double temp = Math.sqrt(go.mass);
            g.drawImage(image.planet, (int)(main.zoom * (go.x-temp/2))+getWidth()/2, 
                    (int)(main.zoom * (go.y-temp/2))+getHeight()/2, 
                    (int)(main.zoom * temp), (int)(main.zoom * temp), null);
        }
        
        
        //draw prediction
        g.setColor(new Color(0, 125, 0));
        for(int i = 1; i < main.pred.size(); i++){
            g.drawLine((int)(main.zoom * main.pred.get(i).getX())+getWidth()/2, 
                    (int)(main.zoom * main.pred.get(i).getY())+getHeight()/2, 
                    (int)(main.zoom * main.pred.get(i-1).getX())+getWidth()/2, 
                    (int)(main.zoom * main.pred.get(i-1).getY())+getHeight()/2);
        }
        
        
        //draw acceleration vector
        g.setColor(Color.red);
        int x = (int)(main.zoom * (main.pred.get(0).getX()))+getWidth()/2,
                y = (int)(main.zoom * (main.pred.get(0).getY()))+getHeight()/2;
        g.drawLine(x, y, (int)(x + main.zoom * 500 * main.pred.get(0).ax), (int)(y + main.zoom * 500 * main.pred.get(0).ay));
        
        
        //draw rocket
        double size = main.rocket.getSkin().getWidth();
        AffineTransform tx = AffineTransform.getRotateInstance(main.rocket.getRotRad(), 
                0.4 * main.zoom * size, 0.4 * main.zoom * size);
        tx.scale(0.8*main.zoom, 0.8*main.zoom);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        g.drawImage(op.filter(main.rocket.getSkin(), null),
                (int)(main.zoom * (main.pred.get(0).getX()-size/2))+getWidth()/2,
                (int)(main.zoom * (main.pred.get(0).getY()-size/2))+getHeight()/2, null);
        
        if(lPr){
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
        
        
        g.setColor(new Color(0, 125, 0));
        
        //show songName
        if(songName != null){
            
            if(songNamePhase < 16){
                g.drawString(songName, getWidth() - 260, songNamePhase);
            }else if(songNamePhase < 116){
                g.drawString(songName, getWidth() - 260, 16);
            }else if(songNamePhase < 132){
                g.drawString(songName, getWidth() - 260, 132 - songNamePhase);
            }else{
                songName = null;
                songNamePhase = 0;
            }
            
            songNamePhase++;
        }
        
        //time indicator
        g.setFont(new Font("Arial", 0, 20));
        g.drawString("time speed", 30, getHeight() - 45);
        
        g.drawRect(20, getHeight() - 40, 120, 20);
        g.fillRect(20, getHeight() - 40, (int)(main.dID * 15), 20);
        
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
        
        super.paint(g);
        
        repaint();
    }
    
    
}
