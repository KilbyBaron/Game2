package com.kilby.game;
import android.graphics.PointF;

import java.util.Random;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Ball extends GameObject{

    float x;
    float y;
    float radius;
    float blastRadius = 50;

    public Ball(float rad, float x, float y){

        setWorldLocation(x, y);
        setType(Type.BALL);
        radius = rad;
        generatePoints();

    }

    public void reset(float x, float y){

        setWorldLocation(x,y);
        setxVelocity(0);
        setyVelocity(0);
    }


    public void generatePoints(){

        int numEdges = 36;
        float[] points = new float[numEdges*3*2+3];
        points[0] = 0;
        points[1] = 0;
        points[2] = 0;
        float angle = 0;
        for (int i = 0; i+4<points.length; i+=6){

            points[i] = (float)(radius*cos(Math.toRadians(angle)));
            points[i+1] = (float)(radius*sin(Math.toRadians(angle)));
            points[i+2] = 0;
            points[i+3] = (float)(radius*cos(Math.toRadians(angle+(360/numEdges))));
            points[i+4] =(float)(radius*sin(Math.toRadians(angle+(360/numEdges))));
            points[i+5] = 0;
            angle += 360/numEdges;

        }

        setVertices(points);

    }

    public void setCoords(int x, int y){
        worldLocation.x = x;
        worldLocation.y = y;
    }


    public void update(float fps){

        move(fps);

    }

    public void launch(int touchx, int touchy){

        float x = touchx - worldLocation.x;
        float y = touchy - worldLocation.y;
        float c = (float)(Math.sqrt(x*x+y*y));
        float ratio = 1000/c;

        setxVelocity(x*ratio);
        setyVelocity(y*ratio);

    }








}// End class