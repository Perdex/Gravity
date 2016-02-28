package gravity;

import java.awt.BorderLayout;
import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.AffineTransformOp;
import java.awt.geom.AffineTransform;
import java.awt.Font;
import java.awt.image.BufferedImage;

public class Draw extends JPanel{
    
    boolean rPr, lPr;
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
                    + "\nzoom: mouse wheel"
                    + "\nreset: R"
                    + "\nmusic: M"
                    + "\nthis info: I"
                    + "\nexit: esc";
    
    public static String songName;
    private int songNamePhase = 0;
    
    public Draw(Gravity main){
        this.main = main;
        
        Actions actions = new Actions(this, main);
        addKeyListener(actions);
        addMouseWheelListener(actions);
        
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
        
        int bgx = (int)main.BGx, bgy = (int)main.BGy,
                offsetX = (int)((bgx + 500) / 1000) * 1000, offsetY = (int)((bgy + 500) / 1000) * 1000;
        for(int i = -1; i <= 1; i++){
            for(int j = -1; j <= 1; j++){
                
                int x1 = 1000 * i + bgx-500 + getWidth()/2 - offsetX,
                        y1 = 1000 * j + bgy-500 + getHeight()/2 - offsetY;
                
                //only draw if window contains one of the corners
                if(contains(x1, y1) || contains(x1, y1 + 1000) || contains(x1 + 1000, y1) || contains(x1 + 1000, y1 + 1000))
                    g.drawImage(image.BG, x1, y1, null);
            }
        }
        
        //update info
        String s2 = "physics time: " + (double)((int)main.FPS)/10 + "ms\ndraw time: " + 
                (int)time + "ms\n" + s + "\n\nacceleration: " + (double)((int)(main.rocket.getA()*100))/100 +
                "\nvelocity: " + (double)((int)(main.rocket.getV()*100))/100 +
                "\ndist from center: " + (int)main.rocket.getD(main.GO[0]) + "\n"; 
        info.setText(s2);
        
        
        //draw planet(s)
        for(int i = 0; i < main.GONum; i++){
            double temp = 10 * Math.sqrt(main.GO[i].mass);
            
            BufferedImage im;
            if(i == 0)
                im = image.planet;
            else
                im = image.moon;
            
            g.drawImage(im, (int)(main.zoom * (main.GO[i].x-temp/2))+getWidth()/2, 
                    (int)(main.zoom * (main.GO[i].y-temp/2))+getHeight()/2, 
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
        
        
        //draw fuel: text
        g.setFont(new Font("Arial", 0, 20));
        g.setColor(new Color(0, 125, 0));
        g.drawString("Fuel", getWidth() - 50, 35);
        //frame
        g.setColor(new Color(200, 200, 200));
        g.drawRect(getWidth() - 40, 40, 20, getHeight() - 59);
        //fuel bar
        g.setColor(new Color((int)((1-main.rocket.fuel) * 255), (int)(main.rocket.fuel * 255), 0));
        g.fillRect(getWidth() - 39, (int)(getHeight() - 19 - main.rocket.fuel * (getHeight() - 60)),
                        19, (int)Math.ceil(main.rocket.fuel * (getHeight() - 60)));
        
        //draw acceleration vector
        g.setColor(Color.red);
        int ax = (int)(main.zoom * (main.rocket.getX()))+getWidth()/2,
                ay = (int)(main.zoom * (main.rocket.getY()))+getHeight()/2;
        g.drawLine(ax, ay, (int)(ax + main.zoom * 500 * main.rocket.lastax), (int)(ay + main.zoom * 500 * main.rocket.lastay));
        
        
        //draw rocket
        double size = main.rocket.getSkin().getWidth();
        AffineTransform tx = AffineTransform.getRotateInstance(main.rocket.getRotRad(), 
                0.4 * main.zoom * size, 0.4 * main.zoom * size);
        tx.scale(0.8*main.zoom, 0.8*main.zoom);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        g.drawImage(op.filter(main.rocket.getSkin(), null),
                (int)(main.zoom * (main.rocket.getX()-size/2))+getWidth()/2,
                (int)(main.zoom * (main.rocket.getY()-size/2))+getHeight()/2, null);
        
        //key functions: rotate rocket and zoom in/out
        if(lPr){
            main.rocket.rot -= 6;
        }if(rPr){
            main.rocket.rot += 6;
        }
        
        
        
        g.setColor(new Color(0, 125, 0));
        //time indicator
        g.setFont(new Font("Arial", 0, 20));
        g.drawString("time speed: " + (double)((int)(main.getDTime() * 10)) / 10, 30, getHeight() - 45);
        
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
