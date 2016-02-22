package gravity;


public class GravityObject {

    public double x = 1, y = 1, xs = 1, ys = 1, xa = 0, 
            ya = 0, mass, lastx = 1, lasty = 1;
    
    public GravityObject(){
        mass = 0;
    }
    
    public GravityObject(double x, double y, double xs, double ys, double m){
        this.x = x;
        this.y = y;
        this.xs = xs;
        this.ys = ys;
        this.mass = m;
        this.lastx = x - xs;
        this.lasty = y - ys;
    }
    public int getX(){
        return (int)x;
    }
    public int getY(){
        return (int)y;
    }
    public double getA(){
        return Math.sqrt(xa*xa + ya*ya);
    }
    public double getD(){
        return Math.sqrt(x*x + y*y);
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
