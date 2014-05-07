package gravity;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Rocket {
    
    private BufferedImage skin, skin2;
    public double rot = 90;
    public int throttle = 0;
    
    public Rocket(){
        initSkin();
    }
    
    
    private void initSkin(){
        try{
            skin = ImageIO.read(getClass().getResource("/images/Rocket.png"));
            skin2 = ImageIO.read(getClass().getResource("/images/Rocket2.png"));
        }catch(IOException e){
            System.out.println("rocket not found");
        }
    }
    
    
    public BufferedImage getSkin(){
        if(throttle == 0)
            return skin;
        else
            return skin2;
    }
    public double getRotRad(){
        return Math.toRadians(rot);
    }
    
    
}
