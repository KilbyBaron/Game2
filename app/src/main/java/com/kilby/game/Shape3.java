package com.kilby.game;

import android.graphics.PointF;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static com.kilby.game.MathHelpers.angleToHorizontal;
import static com.kilby.game.MathHelpers.closestPointOnLine;
import static com.kilby.game.MathHelpers.getPointFromAngle;
import static com.kilby.game.MathHelpers.pointFAddition;
import static com.kilby.game.MathHelpers.pointFSubtraction;

public class Shape3 extends GameObject{

    public ArrayList<Line> edges = new ArrayList();

     //###############  CONSTRUCTOR  ###################

    public Shape3(float x, float y, float[] verts){

        setType(Type.SHAPE);
        setWorldLocation(x,y);
        setVertices(verts);

        for (int i = 0; i < verts.length; i+=6){
            Line newLine = new Line(new PointF(verts[i], verts[i+1]), new PointF(verts[i+3], verts[i+4]));
            edges.add(newLine);
        }
    }



    //###############  SHAPE COLLISION FUNCTIONS   #################



    public boolean checkInside(float px, float py){

        int numIntersections = 0;

        for (Line line : edges){

            PointF v1 = line.v1;
            PointF v2 = line.v2;


            //If line is vertical, just check x values
            if(v1.x-v2.x ==0){
                if (v1.x > px) {
                    if (v1.y > v2.y && py > v2.y && py < v1.y)
                            numIntersections++;
                    else if (v1.y < v2.y && py < v2.y && py > v1.y)
                            numIntersections++;
                }
            }

            else{
                //y = mx+b
                float m = (v1.y - v2.y)/(v1.x-v2.x);
                float b = v1.y - (m*v1.x);

                //if line is horizontal it doesnt intersect
                if (m != 0){
                    PointF PoL = new PointF((py-b)/m, py);


                    if (PoL.x > px && closestPointOnLine(v1,v2, PoL) == PoL){ numIntersections++;}
                }
            }
        }

        if (numIntersections % 2 != 0){return true;}
        else{ return false;}
    }


    public void removeEdgesInsideRadius(PointF location, float radius){

        //Delete all edges with both vertices inside radius
        ArrayList<Line> toRemove = new ArrayList();
        for (Line e : edges){

            if (MathHelpers.distanceBetweenPoints(e.v1, location) < radius &&
                    MathHelpers.distanceBetweenPoints(e.v2, location) < radius) {
                toRemove.add(e);
            }
        }
        for (Line edge : toRemove){
            if (edges.contains(edge)) {
                edges.remove(edge);
            }
        }
    }

    public ArrayList<Line> findNewEdges(ArrayList<PointF> intersections, PointF location, float radius){

        int numIntersections = intersections.size();

        //Sort intersections by angle using a comparator
        Collections.sort(intersections, new MathHelpers.AngleComparator(location));

        //check if first two intersections go through shape
        boolean inside = false;
        double angle1 = angleToHorizontal(intersections.get(0), location);
        double angle2 = angleToHorizontal(intersections.get(1), location);
        double diff = angle2 - angle1;
        if (diff < 0)
            diff += 360;
        double newAngle = angle1 + diff / 2;
        PointF testPoint = MathHelpers.getPointFromAngle(radius, newAngle, location);
        if (checkInside(testPoint.x, testPoint.y))
            inside = true;

        //Cycle through intersections, and collect new edges
        double a1;
        double a2;
        PointF int2;
        PointF newv1;
        PointF newv2;
        ArrayList<Line> newEdges = new ArrayList();
        for (int i = 0; i < numIntersections; i++) {
            if (inside) {


                a1 = angleToHorizontal(intersections.get(i), location);
                if (i == numIntersections - 1)
                    int2 = intersections.get(0);
                else
                    int2 = intersections.get(i + 1);
                a2 = angleToHorizontal(int2, location);
                if (a2 < a1) {
                    a2 += 360;
                }
                double a3 = a1 + 30;

                newv1 = intersections.get(i);
                while (a3 < a2) {

                    newv2 = MathHelpers.getPointFromAngle(radius, a3, location);
                    newEdges.add(new Line(newv1, newv2));
                    newv1 = newv2;
                    a3 += 30;

                }

                newEdges.add(new Line(newv1, int2));
            }
            inside = !inside;
        }

        return newEdges;

    }


    public void hit(PointF location, float radius){

        location = pointFSubtraction(location, worldLocation);
        HashMap<PointF, Line> intMap = new HashMap();
        ArrayList<PointF> intersections = new ArrayList();

        //Get intersection points
        for (int i = 0; i<edges.size(); i++){

            PointF v1 = edges.get(i).v1;
            PointF v2 = edges.get(i).v2;

            //Cycle through each intersection point between edge and circle
            for (PointF p : MathHelpers.getCircleLineIntersectionPoint(v1, v2, location, radius)){

                //Only add the point if it is between the vertices
                if (MathHelpers.closestPointOnLine(v1, v2, p) == p){
                    PointF newp = new PointF(p.x, p.y);
                    intersections.add(newp);
                    intMap.put(newp, edges.get(i));
                }
            }
        }

        //Ignore tangent intersections
        int numIntersections = intersections.size();
        if (numIntersections > 1) {

            ArrayList<Line> newEdges = findNewEdges(intersections, location, radius);

            //delete intersected edges and create links to new edges
            HashMap<Line, PointF> doubleInts = new HashMap();

            for (PointF i : intersections) {
                Line edge = intMap.get(i);

                if (MathHelpers.distanceBetweenPoints(edge.v1, location) < radius)
                    edges.add(new Line(edge.v2, i));
                else if (MathHelpers.distanceBetweenPoints(edge.v2, location) < radius)
                    edges.add(new Line(edge.v1, i));

                //If edge has 2 intersections, link intersections with closest vertex
                else{
                    if (doubleInts.containsKey(edge)){

                        PointF i2 = doubleInts.get(edge);

                        if (MathHelpers.distanceBetweenPoints(edge.v1, i) < MathHelpers.distanceBetweenPoints(edge.v1, i2)){
                            edges.add(new Line(edge.v1, i));
                            edges.add(new Line(edge.v2, i2));
                        }
                        else{
                            edges.add(new Line(edge.v1, i2));
                            edges.add(new Line(edge.v2, i));
                        }
                    }
                    else
                        doubleInts.put(edge, i);
                }
                edges.remove(edge);
            }

            removeEdgesInsideRadius(location, radius);
            for (Line e : newEdges) {
                edges.add(e);
            }
            updateVerts();
        }

    }



    private void updateVerts(){
        float[] newVerts = new float[edges.size()*6];
        for (int i=0; i<edges.size(); i++){
            newVerts[i*6] = edges.get(i).v1.x;
            newVerts[i*6+1] = edges.get(i).v1.y;
            newVerts[i*6+2] = 0;
            newVerts[i*6+3] = edges.get(i).v2.x;
            newVerts[i*6+4] = edges.get(i).v2.y;
            newVerts[i*6+5] = 0;
        }

        setVertices(newVerts);

    }




}

