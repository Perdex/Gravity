package gravity;


public class GravityObject {

    public double x = 1, y = 1, vx = 1, vy = 1, ax = 0, 
            ay = 0, mass;
    
    public GravityObject(){
        mass = 0;
    }
    
    public GravityObject(double x, double y, double xs, double ys, double m){
        this.x = x;
        this.y = y;
        this.vx = xs;
        this.vy = ys;
        this.mass = m;
    }
    public int getX(){
        return (int)x;
    }
    public int getY(){
        return (int)y;
    }
    public double getA(){
        return Math.sqrt(ax*ax + ay*ay);
    }
    public double getV(){
        return Math.sqrt(vx*vx + vy*vy);
    }
    public double getD(GravityObject g){
        double dx = g.x - x, dy = g.y - y;
        return Math.sqrt(dx*dx + dy*dy);
    }
    public void move(double t){
        vx += t * ax;
        vy += t * ay;
        
        ax = 0;
        ay = 0;
        
        x += t * vx;
        y += t * vy;
    }
    
}
