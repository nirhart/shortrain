package com.nirhart.shortrain.train;

public class TrainDirection {
    static public final int RIGHT = 1;
    static public final int LEFT = 1 << 1;
    static public final int DOWN = 1 << 2;
    static public final int UP = 1 << 3;
    static public final int TURN_DOWN = 1 << 4;
    static public final int TURN_UP = 1 << 5;
    static public final int TURN_LEFT = 1 << 6;
    static public final int TURN_RIGHT = 1 << 7;

    private int direction;

    private TrainDirection() {
    }

    static public TrainDirection MakeDirection(int... directions) {
        TrainDirection td = new TrainDirection();
        int direction = 0;
        for (int d : directions) {
            direction |= d;
        }
        td.setDirection(direction);
        return td;
    }

    private void setDirection(int direction) {
        this.direction = direction;
    }

    public void addDirection(int direction) {
        this.direction |= direction;
    }

    boolean isTurningUp() {
        return (this.direction & TURN_UP) > 0;
    }

    boolean isTurningDown() {
        return (this.direction & TURN_DOWN) > 0;
    }

    boolean isTurningLeft() {
        return (this.direction & TURN_LEFT) > 0;
    }

    boolean isTurningRight() {
        return (this.direction & TURN_RIGHT) > 0;
    }

    boolean isHorizontal() {
        return (this.direction & RIGHT) > 0 || (this.direction & LEFT) > 0;
    }
}
