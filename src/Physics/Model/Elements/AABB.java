package Physics.Model.Elements;

import Physics.Model.Computation.Vector2;

import java.awt.*;

public class AABB extends RigidBody implements Texture ,Geometry{
    public Vector2 min;
    public Vector2 max;
    public Color color;

    public AABB(double m, Vector2 g, Vector2 v,double e, double x_min,double y_min,double width, double height,boolean isKe) {
        super(m, g, v,e,isKe);
        color=new Color(0,0,0);
        min=new Vector2(x_min,y_min);
        max=new Vector2(x_min+width,y_min+height);
    }

    //Position update by ticks
    @Override
    public void update(double ticks) {
        min.x=min.x+velocity.x*ticks+0.5*gravity.x*ticks*ticks;
        min.y=min.y+velocity.y*ticks+0.5*gravity.y*ticks*ticks;
        max.x=max.x+velocity.x*ticks+0.5*gravity.x*ticks*ticks;
        max.y=max.y+velocity.y*ticks+0.5*gravity.y*ticks*ticks;
        velocity.x=velocity.x+gravity.x*ticks;
        velocity.y=velocity.y+gravity.y*ticks;
    }

    @Override
    public void update(Vector2 vec) {
        min.x=min.x+vec.x;
        min.y=min.y+vec.y;
        max.x=max.x+vec.x;
        max.y=max.y+vec.y;
    }

    //Implements Interfaces of ITexture and IGeometry
    @Override
    public Vector2 getMin() {
        return new Vector2(min);
    }

    @Override
    public Vector2 getMax() {
        return new Vector2(max);
    }

    @Override
    public Vector2 getExtra() {
        return null;
    }

    @Override
    public Shape getShape() {
        return Shape.Rectangle;
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