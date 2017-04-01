package com.nirhart.shortrain.path;

import android.graphics.Point;

import com.nirhart.shortrain.rail.RailInfo;
import com.nirhart.shortrain.train.TrainDirection;

import java.util.ArrayList;
import java.util.List;

public class TrainPath {

    final private List<PathPoint> path;

    TrainPath() {
        this.path = new ArrayList<>();
    }

    void addPoint(Point point, RailInfo railInfo) {
        TrainDirection direction;
        if (this.path.size() == 0) {
            direction = TrainDirection.MakeDirection(TrainDirection.RIGHT);
        } else {
            TrainDirection lastDirection = this.path.get(this.path.size() - 1).getDirection();
            Point lastPoint = this.path.get(this.path.size() - 1).getPoint();

            if (lastPoint.x == point.x + 1) {
                direction = TrainDirection.MakeDirection(TrainDirection.UP);
                lastDirection.addDirection(TrainDirection.TURN_UP);
            } else if (lastPoint.x == point.x - 1) {
                direction = TrainDirection.MakeDirection(TrainDirection.DOWN);
                lastDirection.addDirection(TrainDirection.TURN_DOWN);
            } else if (lastPoint.y == point.y - 1) {
                direction = TrainDirection.MakeDirection(TrainDirection.RIGHT);
                lastDirection.addDirection(TrainDirection.TURN_RIGHT);
            } else {
                direction = TrainDirection.MakeDirection(TrainDirection.LEFT);
                lastDirection.addDirection(TrainDirection.TURN_LEFT);
            }
        }
        this.path.add(new PathPoint(point, direction, railInfo.getX(), railInfo.getY()));
    }

    void addPoint(int x, int y, RailInfo railInfo) {
        Point point = new Point(x, y);
        addPoint(point, railInfo);
    }

    public List<PathPoint> getPath() {
        return path;
    }
}
