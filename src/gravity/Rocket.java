package gravity;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Rocket extends GravityObject{
    
    private BufferedImage skin, skin2;
    public double rot = 90, fuel;
    public int throttle = 0;
    public boolean thrusting = false;
    
    public Rocket(double x, double y, double xs, double ys, double m){
        super(x, y, xs, ys, m);
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
        if(thrusting)
            return skin2;
        else
            return skin;
    }
    public double getRotRad(){
        return Math.toRadians(rot);
    }
    
    
}
