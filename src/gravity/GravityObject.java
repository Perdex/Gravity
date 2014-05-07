package gravity;


public class GravityObject {

    public double x = 1, y = 1, xs = 1, ys = 1, xa = 0, 
            ya = 0, mass, lastx = 1, lasty = 1;
    
    public GravityObject(){
        mass = 0;
    }
    
    public GravityObject(double ix, double iy, double ixs, double iys, double im){
        x = ix;
        y = iy;
        xs = ixs;
        ys = iys;
        mass = im;
        lastx = ix - xs;
        lasty = iy - ys;
    }
    public int getX(){
        return (int)x;
    }
    public int getY(){
        return (int)y;
    }
    public void move(double d){
        xs += d * xa;
        ys += d * ya;
        
        xa = 0;
        ya = 0;
        
        x += d * xs;
        y += d * ys;
    }
    
}
