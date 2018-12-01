package Game;

import Physics.Controller.PhysicsEngineController;

import java.awt.*;

public class GameController {
    GameRender gr=null;
    GameScene gs=null;
    PhysicsEngineController pc=null;
    public GameController() {
        gs = new GameScene();//JFrame
        gr = new GameRender();//Jpanel
        gs.add(gr);
        gs.setVisible(true);
        //bind gameRneder(Interface Ehysics Eender) to Physics Engine
        pc = new PhysicsEngineController(gr);
        //initial objs
        initialGameWorld();
        //start running
        pc.startPhysicsRunning();
    }

    //To initial the world add objs into physics engine and set gravity
    public void initialGameWorld()
    {
        pc.initialGravity(200);
        pc.initialResistance(0.1,0.2);
        pc.initialWall(-1, 1, 1, 72.5);//Left
        pc.initialWall(0, 73.6, 118, 1);//Bottom
        pc.initialWall(118, 1, 1, 72.5);//Right
        pc.initialWall(0, -1, 118, 1);//Up

        pc.initialBall(1, -10, -10, 0.92, 80, 3, 2, new Color(255, 0, 0));
        pc.initialBall(1, 20, 10, 0.92, 70, 3, 2, new Color(0, 255, 0));
        pc.initialBall(1, 10, 30, 0.92, 50.3, 10, 2, new Color(0, 0, 255));
        pc.initialBall(1, -10, -10, 0.92, 10, 8, 2, new Color(255, 0, 0));
        pc.initialBall(1, 20, 10, 0.92, 20, 3, 6, new Color(0, 255, 0));
        pc.initialBall(1, 10, 30, 0.92, 40, 30, 2, new Color(0, 0, 255));

        pc.initialBox(5, -30, -10, 0.9, 70, 20, 2, 3, new Color(255, 255, 0));
        pc.initialBox(5, 30, 0, 0.9, 60, 20, 5, 4, new Color(255, 0, 255));
        pc.initialBox(5, -30, 20, 0.9, 100, 55, 4, 2, new Color(0, 255, 255));

        pc.initialTriangle(10, 0, 0, 1, 50, 40,10 , 10, 2, new Color(87,145,4));

        pc.initialRotationRectangle(49, 55, 2, 15, false, 'z');
        pc.initialRotationRectangle(81, 55, 2, 15, true, 'x');
    }
}
