package com.nirhart.shortrain.path;

import android.graphics.Point;

import com.nirhart.shortrain.train.TrainDirection;

public class PathPoint {

    final private Point point;
    final private int left, top;
    final private TrainDirection direction;

    PathPoint(Point point, TrainDirection direction, int left, int top) {
        this.point = point;
        this.direction = direction;
        this.left = left;
        this.top = top;
    }

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    Point getPoint() {
        return point;
    }

    public TrainDirection getDirection() {
        return direction;
    }
}
