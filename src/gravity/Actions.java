package gravity;

import java.util.ArrayList;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import gravity.audio.Audio;

public class Actions implements KeyListener{
    
    private final Draw d;
    private final Gravity main;
    private final ArrayList<Integer> keys;
    
    public Actions(Draw d, Gravity g){
        this.d = d;
        this.main = g;
        keys = new ArrayList();
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
                main.rocket.throttle = 1;
                Audio.ROCKET_NOISE.play();
                break;
            case KeyEvent.VK_A:
                d.lPr = true;
                break;
            case KeyEvent.VK_D:
                d.rPr = true;
                break;
            case KeyEvent.VK_E:
                if(main.dToGo == main.dID)
                    if(main.dToGo < 7.5)
                        main.dToGo += 0.5;
                    else
                        main.dToGo = 8;
                break;
            case KeyEvent.VK_Q:
                
                //make sure there's no pending acceleration
                main.dToGo = main.dID;
                
                if(main.dToGo > 0.55)
                    main.dToGo -= 0.5;
                else
                    main.dToGo = 0.05;
                break;
            case KeyEvent.VK_R:
                main.reset = -1;
                break;
            case KeyEvent.VK_C:
                if(e.getKeyChar() == 'C'){
                    main.cameraMode--;
                    if(main.cameraMode == -2)
                        main.cameraMode = main.GONum-1;
                }else{
                    main.cameraMode++;
                    if(main.cameraMode == main.GONum)
                        main.cameraMode = -1;
                }
                    
                break;
            case KeyEvent.VK_I:
                d.info.setVisible(!d.info.isVisible());
                break;
            case KeyEvent.VK_M:
                Audio.toggleMusic();
                break;
            case KeyEvent.VK_COMMA:
                d.commaPr = true;
                break;
            case KeyEvent.VK_PERIOD:
                d.dotPr = true;
                break;
            case KeyEvent.VK_ESCAPE:
                Gravity.fr.dispose();
                Gravity.audio.stop();
                main.loop = false;
                break;
        }
        
    }//keyDown
    
    @Override
    public void keyReleased(KeyEvent e){
        int key = e.getKeyCode();
        
        keys.remove((Integer)key);
        
        switch(key){
            case KeyEvent.VK_W:
                main.rocket.throttle = 0;
                Audio.ROCKET_NOISE.stop();
                break;
            case KeyEvent.VK_A:
                d.lPr = false;
                break;
            case KeyEvent.VK_D:
                d.rPr = false;
                break;
            case KeyEvent.VK_COMMA:
                d.commaPr = false;
                break;
            case KeyEvent.VK_PERIOD:
                d.dotPr = false;
                break;
        }
        
    }
    
    @Override
    public void keyTyped(KeyEvent e){}
}
