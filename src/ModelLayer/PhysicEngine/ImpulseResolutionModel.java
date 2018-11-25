package ModelLayer.PhysicEngine;

import ModelLayer.Computation.MathD;
import ModelLayer.Computation.Matrix2;
import ModelLayer.Computation.Vector2;

import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;

public class ImpulseResolutionModel {
    RigidBody A;
    RigidBody B;
    double penetration;
    Vector2 normal;

    public ImpulseResolutionModel(RigidBody a, RigidBody b)
    {
        boolean isCollided=false;
        A=a;
        B=b;
        if(A instanceof Circle && B instanceof Circle)
        {
            isCollided=CirclevsCircle((Circle) A, (Circle)B);
        }
        else if (A instanceof AABB && B instanceof AABB)
        {
            isCollided=AABBvsAABB((AABB)A, (AABB)B);
        }
        else if(A instanceof AABB&& B instanceof Circle){
            isCollided=AABBvsCircle((AABB)A, (Circle)B);

        }
        else if(A instanceof Circle&& B instanceof AABB){
            RigidBody C=B;
            B=A;
            A=C;
            isCollided=AABBvsCircle((AABB)A, (Circle)B);
        }
        else if(A instanceof RotationRectangle&& B instanceof Circle){
           isCollided=RotationRectanglevsCircle((RotationRectangle)A, (Circle)B);

        }
        else if(A instanceof Circle&& B instanceof RotationRectangle){
            RigidBody C=B;
            B=A;
            A=C;
            isCollided=RotationRectanglevsCircle((RotationRectangle)A, (Circle)B);
        }
        if(isCollided)
        {

            ResolveCollision();
            PositionalCorrection();
        }

    }

    void PositionalCorrection( )
    {

        double percent = 0.2; // usually 20% to 80%
        double slop = 0.01; // usually 0.01 to 0.1
        double thres= (penetration - slop)>= 0.0?(penetration - slop):0.0;
        double coeff= thres / (A.massInv + B.massInv) * percent;
        Vector2 correction =Vector2.multiply(normal, coeff);
//        if(A.massInv!=0&&B.massInv==0)
//        {
//            A.update(Vector2.multiply(correction, -A.massInv));
//            return;
//        }
//        if(A.massInv==0&&B.massInv!=0)
//        {
//            B.update(Vector2.multiply(correction, B.massInv));
//            return ;
//        }
        if(A.massInv!=0)
            A.update(Vector2.multiply(correction, -A.massInv));
        if(B.massInv!=0)
            B.update(Vector2.multiply(correction, B.massInv));


    }


    void ResolveCollision()
    {
        // Calculate relative velocity
        Vector2 rv =Vector2.minus(B.velocity, A.velocity);

        // Calculate relative velocity in terms of the normal direction
        double velAlongNormal = rv.dot(normal);

        // Do not resolve if velocities are separating
        if(velAlongNormal > 0)
            return;

        // Calculate restitution
        double e = min(A.epsilon, B.epsilon);

        // Calculate impulse scalar
        double j = -(1 + e) * velAlongNormal;
        j /=( A.massInv + B.massInv);

        // Apply impulse
        Vector2 impulse =Vector2.multiply(normal, j);
        A.velocity.minusby(Vector2.multiply(impulse, A.massInv));
        B.velocity.addby(Vector2.multiply(impulse, B.massInv));
    }

    boolean AABBvsAABB( AABB a, AABB b )
    {
        // Vector from A to B
        Vector2 n =new Vector2((b.max.x+b.min.x)/2.0-(a.max.x+a.min.x)/2.0,(b.max.y+b.min.y)/2.0-(a.max.y+a.min.y)/2.0);
        // Calculate half extents along x axis for each object
        double a_extent = (a.max.x - a.min.x) / 2.0;
        double b_extent = (b.max.x - b.min.x) / 2.0;
       // Calculate overlap on x axis
        double x_overlap = a_extent + b_extent - abs( n.x );
        // SAT test on x axis
        if(x_overlap > 0)
        {
            // Calculate overlap on y axis
            double y_overlap = (b.max.y - b.min.y) / 2.0 + (a.max.y - a.min.y) / 2.0 - abs( n.y );
//            // SAT test on y axis
            if(y_overlap > 0)
            {
                // Find out which axis is axis of least penetration
                //Use instant velocity to count time
               double relativeVelocityVx=(abs(b.velocity.x-a.velocity.x));
                double relativeVelocityVy=(abs(b.velocity.y-a.velocity.y));
                if(relativeVelocityVx==0&&relativeVelocityVy==0)
                {
                    relativeVelocityVx=1;relativeVelocityVy=1;
                }
                if(relativeVelocityVx!=0&&(relativeVelocityVy==0||x_overlap/relativeVelocityVx < y_overlap/relativeVelocityVy))
                {
                    // Point towards B knowing that n points from A to B
                    if(n.x < 0)
                        normal =new  Vector2( -1, 0 );
                    else
                        normal = new Vector2(1,0);
                    penetration = x_overlap;
                    return true;
                }
                else if (relativeVelocityVy!=0&&(relativeVelocityVx==0||x_overlap/relativeVelocityVx >= y_overlap/relativeVelocityVy))
                {
                    // Point toward B knowing that n points from A to B
                    if(n.y < 0)
                        normal = new  Vector2( 0, -1 );
                    else
                        normal = new  Vector2( 0, 1 );
                    penetration = y_overlap;
                    return true;
                }
            }
        }
        return false;
    }

    boolean CirclevsCircle( Circle a, Circle b ) {
        // Vector from A to B
        Vector2 n = Vector2.minus(b.position,a.position );
        double r = a.radius + b.radius;
        r *= r;
        if(n.lengthSquared( ) > r)
            return false;
        // Circles have collided, now compute manifold
        double d = n.length(); // perform actual sqrt

        // If distance between circles is not zero
        if(d != 0)
        {
            // Distance is difference between radius and distance
            penetration = a.radius + b.radius - d;
            // Utilize our d since we performed sqrt on it already within Length( )
            // Points from A to B, and is a unit vector
            normal = Vector2.multiply(n, 1.0/d);
            return true;
        }
        // Circles are on same position
        else
        {
            // Choose random (but consistent) values
            penetration = a.radius;
            normal = new Vector2( 1, 0 );
            return true;
        }
    }

    boolean AABBvsCircle( AABB a, Circle b )
    {

        // Vector from A to B
        Vector2 n = new Vector2(b.position.x-(a.min.x+a.max.x)/2.0,b.position.y-(a.min.y+a.max.y)/2.0);

        // Closest point on A to center of B
        Vector2 closest = new Vector2(n);

        // Calculate half extents along each axis
        double x_extent = (a.max.x - a.min.x) / 2;
        double y_extent = (a.max.y - a.min.y) / 2;

        // Clamp point to edges of the AABB
        closest.x = MathD.clamp( closest.x,-x_extent, x_extent );
        closest.y = MathD.clamp( closest.y, -y_extent, y_extent );

        boolean inside = false;

        // Circle is inside the AABB, so we need to clamp the circle's center
        // to the closest edge
        if(n .equals(closest) )
        {
            inside = true;

            // Find closest axis
            if(x_extent-abs( n.x ) < y_extent-abs( n.y ))
            {
                // Clamp to closest extent
                if(closest.x > 0)
                    closest.x = x_extent;
                else
                    closest.x = -x_extent;
            }
            // y axis is shorter
            else
            {
                // Clamp to closest extent
                if(closest.y > 0)
                    closest.y = y_extent;
                else
                    closest.y = -y_extent;
            }
        }

        normal = Vector2.minus(n, closest);
        double d = normal.lengthSquared( );
        double r = b.radius;

        // Early out of the radius is shorter than distance to closest point and
        // Circle not inside the AABB
        if(d > r * r && !inside)
            return false;

        // Avoided sqrt until we needed
        d = sqrt( d );

        // Collision normal needs to be flipped to point outside if circle was
        // inside the AABB
        if(inside)
        {
            normal.multiplyBy(-1);
            normal.normalize();
            penetration = r - d;
        }
        else
        {
            normal.normalize();
            penetration = r - d;
        }
        return true;
    }


    boolean RotationRectanglevsCircle( RotationRectangle a, Circle b )
    {

        // Vector from A to B
        Vector2 n = new Vector2(b.position.x-(a.endPoint.x+a.length/2.0*Math.cos(a.radian)),b.position.y-(a.endPoint.y+a.length/2.0*Math.sin(a.radian)));
        double radius=a.radian-Math.PI/2.0;
        double cosRadius=Math.cos(radius);
        double sinRadius=Math.sin(radius);
        n= Matrix2.multiplyVector2(new Matrix2(cosRadius,sinRadius,-sinRadius,cosRadius), n);
        // Closest point on A to center of B
        Vector2 closest = new Vector2(n);

        // Calculate half extents along each axis
        double x_extent = a.widtd/2.0;
        double y_extent = a.length/2.0;

        // Clamp point to edges of the AABB
        closest.x = MathD.clamp( closest.x,-x_extent, x_extent );
        closest.y = MathD.clamp( closest.y, -y_extent, y_extent );

        boolean inside = false;

        // Circle is inside the AABB, so we need to clamp the circle's center
        // to the closest edge
        if(n .equals(closest) )
        {
            inside = true;

            // Find closest axis
            if(x_extent-abs( n.x ) < y_extent-abs( n.y ))
            {
                // Clamp to closest extent
                if(closest.x > 0)
                    closest.x = x_extent;
                else
                    closest.x = -x_extent;
            }
            // y axis is shorter
            else
            {
                // Clamp to closest extent
                if(closest.y > 0)
                    closest.y = y_extent;
                else
                    closest.y = -y_extent;
            }
        }

        normal = Vector2.minus(n, closest);
        double d = normal.lengthSquared( );
        double r = b.radius;

        // Early out of the radius is shorter than distance to closest point and
        // Circle not inside the AABB
        if(d > r * r && !inside)
            return false;

        // Avoided sqrt until we needed
        d = sqrt( d );

        // Collision normal needs to be flipped to point outside if circle was
        // inside the AABB

        double armforce=(y_extent+closest.y<a.length)?y_extent+closest.y:a.length;
        a.velocity=a.getSpeed(armforce);
        if(inside)
        {
            normal.multiplyBy(-1);
            normal.normalize();

            normal=Matrix2.multiplyVector2(new Matrix2(cosRadius,-sinRadius,sinRadius,cosRadius), normal);
            penetration = r - d;
        }
        else
        {
            normal.normalize();
            normal=Matrix2.multiplyVector2(new Matrix2(cosRadius,-sinRadius,sinRadius,cosRadius), normal);
            penetration = r - d;
        }
        return true;
    }

}
