package gravity;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Images {

    public BufferedImage BG, planet, moon;
    
    public Images(){
        try{
            BG = ImageIO.read(getClass().getResource("/images/BG.jpeg"));
            planet = ImageIO.read(getClass().getResource("/images/Planet.png"));
            moon = ImageIO.read(getClass().getResource("/images/Moon.png"));
        }catch(IOException e){
            System.out.println("images not found");
        }
    }
}
