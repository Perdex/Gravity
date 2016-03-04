package gravity;

import java.util.ArrayList;
import java.awt.event.*;

import gravity.audio.Audio;

public class Actions implements KeyListener, MouseWheelListener{
    
    private final Draw d;
    private final Gravity g;
    private final ArrayList<Integer> keys;
    
    public Actions(Draw d, Gravity g){
        this.d = d;
        this.g = g;
        keys = new ArrayList<>();
    }//Actions
    
    @Override
    public void keyPressed(KeyEvent e){
        if(!keys.contains(e.getKeyCode())){
            keys.add(e.getKeyCode());
            keyDown(e);
        }
    }//keyPressed
    
    public void keyDown(KeyEvent e){
        
        switch(e.getKeyCode()){
            case KeyEvent.VK_W:
                g.rocket.throttle = 1;
                break;
            case KeyEvent.VK_A:
                d.lPr = true;
                break;
            case KeyEvent.VK_D:
                d.rPr = true;
                break;
            case KeyEvent.VK_E:
                if(g.dID < g.times.length - 1)
                    g.dID++;
                break;
            case KeyEvent.VK_Q:
                if(g.dID > 0)
                    g.dID--;
                break;
            case KeyEvent.VK_R:
                g.reset = -1;
                break;
            case KeyEvent.VK_C:
                if(e.getKeyChar() == 'C'){
                    g.cameraMode--;
                    if(g.cameraMode == -2)
                        g.cameraMode = g.GONum-1;
                }else{
                    g.cameraMode++;
                    if(g.cameraMode == g.GONum)
                        g.cameraMode = -1;
                }
                    
                break;
            case KeyEvent.VK_I:
                d.info.setVisible(!d.info.isVisible());
                break;
            case KeyEvent.VK_M:
                Audio.toggleMusic();
                break;
            case KeyEvent.VK_ESCAPE:
                Gravity.fr.dispose();
                
                while(Gravity.audio == null)
                    try{Thread.sleep(50);}catch(InterruptedException ex){}
                Gravity.audio.stop();
                
                g.loop = false;
                try{
                    Thread.sleep(1000);
                }catch(InterruptedException ex){}
                System.exit(0);
                break;
        }
        
    }//keyDown
    
    @Override
    public void keyReleased(KeyEvent e){
        int key = e.getKeyCode();
        
        keys.remove((Integer)key);
        
        switch(key){
            case KeyEvent.VK_W:
                g.rocket.throttle = 0;
                Audio.ROCKET_NOISE.stop();
                break;
            case KeyEvent.VK_A:
                d.lPr = false;
                break;
            case KeyEvent.VK_D:
                d.rPr = false;
                break;
        }
        
    }
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e){
        if(e.getUnitsToScroll() < 0){
            if(g.zoom < 1)
                g.zoom *= 1.05;
        }else{
            if(g.zoom > 0.02)
                g.zoom *= 0.95;
        }
    }//mouseWheelMoved
    
    @Override
    public void keyTyped(KeyEvent e){}
}
