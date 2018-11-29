package Physics.Model.Elements;

import Physics.Model.Computation.Vector2;

import java.awt.*;

public class Triangle extends RigidBody  implements Texture ,Geometry{
    public Vector2 min;
    public double len1;
    public double len2;
    public Vector2 radian; // now it is (1,1), (1,-1),(-1,1),(-1,-1)
    public Color color;

    public Triangle(double m, Vector2 f, Vector2 v, double e,double x,double y,double l1,double l2,int type) {
        super(m, f, v, e);
        this.color=new Color(0,0,0);
        min=new Vector2(x,y);
        len1=l1;
        len2=l2;
        switch(type)
        {
            case 1:
            {
                radian=new Vector2(1,1);
                break;
            }
            case 2:
            {
                radian=new Vector2(1,-1);
                break;
            }
            case 3:
            {
                radian=new Vector2(-1,1);
                break;
            }
            case 4:
            {
                radian=new Vector2(-1,-1);
                break;
            }
            default:
            {
                radian=new Vector2(1,1);
                break;
            }
        }

    }

    @Override
    public Vector2 getMin() {
        return new Vector2(min);
    }

    @Override
    public Vector2 getMax() {
        return new Vector2(len1,len2);
    }

    @Override
    public Vector2 getExtra() {
        return new Vector2(radian);
    }

    @Override
    public Shape getShape() {
        return Shape.Triangle;
    }

    @Override
    public void update(double ticks) {
        min.x=min.x+velocity.x*ticks+0.5*force.x*massInv*ticks*ticks;
        min.y=min.y+velocity.y*ticks+0.5*force.y*massInv*ticks*ticks;
        velocity.x=velocity.x+force.x*massInv*ticks;
        velocity.y=velocity.y+force.y*massInv*ticks;
    }

    @Override
    public void update(Vector2 vec) {
        min.x=min.x+vec.x;
        min.y=min.y+vec.y;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
        this.color=color;
    }
}