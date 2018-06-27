package com.kilby.game;

import android.graphics.Point;
import android.graphics.PointF;

import static java.lang.Math.sqrt;

public class Shape extends GameObject{

    public Box[] boxes;
    private float[] vertices;


    public Shape(Point location, float[] verts, float screenWidth, float screenHeight){
        setType(Type.SHAPE);
        setWorldLocation(location.x,location.y);
        vertices = verts;
        setVertices(vertices);
        fillBoxes(screenWidth, screenHeight);
    }


    private void fillBoxes(float screenWidth, float screenHeight){

        //Define rectangle which contains shape
        float maxX = vertices[0];
        float minX = vertices[0];
        float maxY = vertices[1];
        float minY = vertices[1];
        for (int i = 0; i < vertices.length; i+=3){
            if (vertices[i] > maxX){maxX = vertices[i];}
            if (vertices[i] < minX){minX = vertices[i];}
            if (vertices[i+1] > maxY){maxY = vertices[i+1];}
            if (vertices[i+1] < minY){minY = vertices[i+1];}
        }
        width = maxX - minX;
        length = maxY - minY;

        //create a temporary box to get the box dimensions
        Box tempBox = new Box(screenWidth, screenHeight, 0,0);
        float boxWidth  = tempBox.width;
        float boxHeight  = tempBox.length;

        //Set temp box to null so that it will be removed by garbage collector
        tempBox = null;

        //Create temporary array to store box coords
        float[] boxCoords = new float[(int)width*(int)length*2];

        //Imagine the containing box is filled with potential boxes
        //Check if each potential box is within shape, if so add point to temporary boxCoord list
        int numBoxes = 0;

        float px = minX + boxWidth/2;
        for (int x = 0; x*boxWidth < width; x++){


            float py = minY + boxHeight/2;
            for (int y=0; y*boxHeight<length; y++){

                if (checkInside(px, py)){

                    boxCoords[numBoxes*2] = px + worldLocation.x;
                    boxCoords[numBoxes*2+1] = py + worldLocation.y;
                    numBoxes++;
                }
                py += boxHeight;
            }
            px += boxWidth;
        }

        boxes = new Box[numBoxes];

        for (int z = 0; z<numBoxes; z++){
            boxes[z] = new Box(screenWidth, screenHeight, boxCoords[z*2], boxCoords[z*2+1]);
        }
    }


    private boolean checkInside(float px, float py){

        int numIntersections = 0;
        for (int i = 0; i <= vertices.length-3; i+=3){

            PointF v1 = new PointF(vertices[i], vertices[i+1]);

            //If next point is past list, set second point to first point
            PointF v2;
            if (i+4 > vertices.length){
                v2 = new PointF(vertices[0], vertices[1]);
            }
            else {
                v2 = new PointF(vertices[i + 3], vertices[i+4]);
            }

            //If line is vertical, just check x values
            if(v1.x-v2.x ==0){
                if (v1.x > px){ numIntersections++;}
            }

            else{
                //y = mx+b
                float m = (v1.y - v2.y)/(v1.x-v2.x);
                float b = v1.y - (m*v1.x);

                //if line is horizontal it doesnt intersect
                if (m != 0){
                    float x = (py-b)/m;

                    if (x > px){ numIntersections++;}
                }
            }
        }

        if (numIntersections % 2 != 0){return true;}
        else{ return false;}
    }

    public boolean contains(int x, int y){

        if (checkInside(x - worldLocation.x, y-worldLocation.y)){
            System.out.println("HIT");
            return true;
        }
        return false;
    }

    public void hit(int x, int y){
        float hitX = x;
        float hitY = y;

        System.out.println(hitX+","+hitY);

        for (Box box : boxes){
                float xDiff = hitX-box.worldLocation.x;
                float yDiff = hitY-box.worldLocation.y;
                double dist = sqrt(xDiff*xDiff + yDiff*yDiff);
            //System.out.println(box.worldLocation);
                if (dist <100) {

                    box.deactivate();
                }
        }
    }


}