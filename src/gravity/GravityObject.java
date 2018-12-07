package gravity;


public class GravityObject {

    public double x = 1, y = 1, vx = 1, vy = 1, ax = 0, 
            ay = 0, mass, lastax = 0, lastay = 0;
    
    public GravityObject(int gosize){
        mass = 0;
    }
    
    public GravityObject(double x, double y, double xs, double ys, double m){
        this.x = x;
        this.y = y;
        this.vx = xs;
        this.vy = ys;
        this.mass = m;
    }
    
    
    //copy from another
    public GravityObject(GravityObject g, GravityObject[] GO, double t){
        
        ax = g.ax;
        ay = g.ay;

        vx = g.vx;
        vy = g.vy;

        x = g.x;
        y = g.y;
        
        
        for(int i = 1; i < GO.length; i++){
            Gravity.fall(GO[i], GO[0]);
            GO[i].move(t);
        }
        
    }//copyTo
    
    public int getX(){
        return (int)x;
    }
    public int getY(){
        return (int)y;
    }
    public double getA(){
        return Math.hypot(lastax, lastay);
    }
    public double getV(){
        return Math.hypot(vx, vy);
    }
    public double getD(GravityObject g){
        return Math.hypot(g.x - x, g.y - y);
    }
    public void move(double t){
        vx += t * ax;
        vy += t * ay;
        
        lastax = ax;
        lastay = ay;
        
        ax = 0;
        ay = 0;
        
        x += t * vx;
        y += t * vy;
    }//move
    
    
    public static void copy(GravityObject[] from, GravityObject[] to){
        for(int i = 0; i < from.length; i++){
            to[i].x = from[i].x;
            to[i].y = from[i].y;
            to[i].vx = from[i].vx;
            to[i].vy = from[i].vy;
            to[i].ax = from[i].ax;
            to[i].ay = from[i].ay;
            to[i].mass = from[i].mass;
            to[i].lastax = from[i].lastax;
            to[i].lastay = from[i].lastay;
        }
    
    }//copy
    
}
