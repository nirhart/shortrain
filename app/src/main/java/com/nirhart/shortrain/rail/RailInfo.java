package com.nirhart.shortrain.rail;

import com.nirhart.shortrain.path.PathParser;

public class RailInfo {

    final public static int NOT_SET = PathParser.RAIL_OFFSET;
    final public static int HORIZONTAL = PathParser.RAIL_OFFSET + 1;
    final public static int VERTICAL = PathParser.RAIL_OFFSET + 2;
    final public static int TOP_LEFT_CORNER = PathParser.RAIL_OFFSET + 3;
    final public static int TOP_RIGHT_CORNER = PathParser.RAIL_OFFSET + 4;
    final public static int BOTTOM_LEFT_CORNER = PathParser.RAIL_OFFSET + 5;
    final public static int BOTTOM_RIGHT_CORNER = PathParser.RAIL_OFFSET + 6;

    final private int x, y, rotation;

    public RailInfo(int x, int y, int rotation) {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getRotation() {
        return rotation;
    }

    @Override
    public String toString() {
        return "Wall[" + x + ", " + y + "] rotation: " + rotation;
    }
}
