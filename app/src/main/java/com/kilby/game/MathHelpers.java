package com.kilby.game;

import android.graphics.PointF;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

public class MathHelpers {

    //###############  MATH HELPER FUNCTIONS   #################

    public static boolean isPointOnLine(PointF v1, PointF v2, PointF p){

        //If its a vertical line, only check if point has the same x value.
        if (v1.x - v2.x == 0) {
            if (v1.x == p.x) {
                if (closestPointOnLine(v1, v2, p) == p)
                    return true;
            }
        }

        //If its not vertical, find line's equation, and compare calculated y with actual y
        else {
            float m = (v1.y - v2.y) / (v1.x - v2.x);
            float b = v1.y - m * v1.x;
            float calcY = m * p.x + b;

            if (p.y == calcY) {
                if (closestPointOnLine(v1, v2, p) == p)
                    return true;
            }
        }

        return false;
    }

    /*This function takes in a line segment and a point on that line to infinity.
     *If the point is on the line segment it returns the point, otherwise it returns the closest end of the line segment */
    public static PointF closestPointOnLine(PointF v1, PointF v2, PointF p){
        PointF v2Subv1 = new PointF(v2.x-v1.x, v2.y-v1.y);
        PointF pSubv1 = new PointF(p.x-v1.x, p.y-v1.y);

        float r = dotProduct(v2Subv1, pSubv1)/(v2Subv1.x*v2Subv1.x + v2Subv1.y*v2Subv1.y);

        if (r <= 0)
            p = v1;
        else if (r > 1)
            p = v2;

        return p;
    }

    public static PointF pointLineIntersection(float x, float y, PointF v1, PointF v2){

        PointF intersection = new PointF();

        if ((v1.x-v2.x) == 0){
            intersection.x = v1.x;
            intersection.y = y;
        }

        else if ((v1.y-v2.y) == 0){
            intersection.x = x;
            intersection.y = v1.y;
        }

        else {

            float linem = (v1.y - v2.y) / (v1.x - v2.x);
            float lineb = v1.y - linem * v1.x;

            float perpm = -1 / linem;
            float perpb = y - perpm * x;

            intersection.x = (perpb - lineb) / (linem - perpm);
            intersection.y = perpm * intersection.x + perpb;
        }

        //If intersection is not on line segment, return closest end of line segment
        return closestPointOnLine(v1, v2, intersection);
    }

    public static double distanceFromPointToLine(PointF p, PointF v1, PointF v2){

        PointF closestPointOnLine = MathHelpers.pointLineIntersection(p.x, p.y, v1, v2);

        return distanceBetweenPoints(closestPointOnLine, p);

    }


    public static List<PointF> getCircleLineIntersectionPoint(PointF pointA,
                                                              PointF pointB, PointF center, double radius) {
        double baX = pointB.x - pointA.x;
        double baY = pointB.y - pointA.y;
        double caX = center.x - pointA.x;
        double caY = center.y - pointA.y;

        double a = baX * baX + baY * baY;
        double bBy2 = baX * caX + baY * caY;
        double c = caX * caX + caY * caY - radius * radius;

        double pBy2 = bBy2 / a;
        double q = c / a;

        double disc = pBy2 * pBy2 - q;
        if (disc < 0) {
            return Collections.emptyList();
        }
        // if disc == 0 ... dealt with later
        double tmpSqrt = Math.sqrt(disc);
        double abScalingFactor1 = -pBy2 + tmpSqrt;
        double abScalingFactor2 = -pBy2 - tmpSqrt;

        PointF p1 = new PointF((float)(pointA.x - baX * abScalingFactor1), (float)(pointA.y
                - baY * abScalingFactor1));
        if (disc == 0) { // abScalingFactor1 == abScalingFactor2
            return Collections.singletonList(p1);
        }
        PointF p2 = new PointF((float)(pointA.x - baX * abScalingFactor2), (float)(pointA.y
                - baY * abScalingFactor2));
        return Arrays.asList(p1, p2);
    }


    public static double distanceBetweenPoints(PointF p1, PointF p2){
        PointF diff = pointFSubtraction(p1, p2);
        return sqrt(diff.x*diff.x + diff.y*diff.y);
    }

    public static float dotProduct(PointF a, PointF b){
        return a.x*b.x + a.y*b.y;
    }

    public static PointF pointFAddition(PointF p1, PointF p2){
        return new PointF(p1.x+p2.x, p1.y+p2.y);
    }

    public static PointF pointFSubtraction(PointF p1, PointF p2){
        return new PointF(p1.x-p2.x, p1.y-p2.y);
    }

    public static double angleToHorizontal(PointF p, PointF l){
        double a = toDegrees(java.lang.Math.atan2((double)(p.y-l.y),(double)(p.x-l.x)));
        if (a<0)
            a += 360;
        return a;
    }

    public static PointF getPointFromAngle(float r, double a, PointF l){
        PointF p = new PointF((float) (r*cos(toRadians(a))+l.x), (float)(r*sin(toRadians(a))+l.y));
        return p;
    }

    public static class AngleComparator implements Comparator<PointF> {
        PointF location;
        AngleComparator(PointF l){
            location = l;
        }

        @Override
        public int compare(PointF v1, PointF v2) {

            if (angleToHorizontal(v1,location) < angleToHorizontal(v2,location))
                return -1;
            else if (angleToHorizontal(v1,location) == angleToHorizontal(v2,location))
                return 0;
            else
                return 1;
        }
    }



}
