package com.kilby.game;

import android.graphics.PointF;

import java.util.Random;

public class Box extends GameObject{

    public Box(float mapWidth, float mapHeight, float x, float y){

        setType(Type.BOX);
        int SIZERATIO = 50;
        setSize(mapWidth/SIZERATIO, mapWidth/SIZERATIO);
        setWorldLocation(x,y);
        isActive = false;

        // The vertices of the border represent four lines
        // that create a border of a size passed into the constructor
        float[] borderVertices = new float[]{
                // A line from point 1 to point 2
                - width/2, -length/2, 0,
                width/2, -length/2, 0,
                // Point 2 to point 3
                width/2, -length/2, 0,
                width/2, length/2, 0,
                // Point 3 to point 4
                width/2, length/2, 0,
                -width/2, length/2, 0,
                // Point 4 to point 1
                -width/2, length/2, 0,
                - width/2, -length/2, 0,
        };

        setVertices(borderVertices);

    }

    public void update(float fps){

        if (isActive) {

            setyVelocity(getyVelocity()-40);

            move(fps);
        }

    }


    public void hit(PointF location){
        isActive = true;
        setWorldLocation(location.x, location.y);
        launch();
    }


    public void launch(){


        double x = Math.random()*2-1;
        double y = Math.random()*2-1;
        float c = (float)(Math.sqrt(x*x+y*y));
        double speed = Math.random()*1000;
        double ratio = speed/c;

        setxVelocity((float)(x*ratio));
        setyVelocity((float)(y*ratio));

    }



}