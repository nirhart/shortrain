package com.nirhart.shortrain.path;

import android.graphics.Point;
import android.graphics.Rect;

import com.nirhart.shortrain.rail.RailInfo;
import com.nirhart.shortrain.train.TrainDirection;

import java.util.List;

public class PathParser {

    public static final int RAIL_OFFSET = 1000;

    private final int screenWidth;
    private final int screenHeight;

    public PathParser(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public TrainPath parse(int startX, int startY, Rect tileRect, List<RailInfo> rails) {
        TrainPath trainPath = new TrainPath();
        int direction = TrainDirection.RIGHT; // trains always starts to right

        int row = startY / tileRect.height();
        int col = startX / tileRect.width();
        RailInfo lastRailInfo = new RailInfo(startX, startY, TrainDirection.RIGHT);
        trainPath.addPoint(row, col, lastRailInfo);
        RailInfo[][] board = buildBoard(tileRect, rails);
        Point nextTile;
        while ((nextTile = getNextTile(board, row, col, direction)) != null &&
                (direction = changeDirection(direction, board[nextTile.x][nextTile.y].getRotation())) != -1) {
            lastRailInfo = board[nextTile.x][nextTile.y];
            trainPath.addPoint(nextTile, lastRailInfo);
            row = nextTile.x;
            col = nextTile.y;
        }

        Point nextTilePoint = getNextTilePoint(row, col, direction);

        switch (direction) {
            case TrainDirection.LEFT:
                lastRailInfo = new RailInfo(lastRailInfo.getX() - tileRect.width(), lastRailInfo.getY(), 0);
                break;
            case TrainDirection.RIGHT:
                lastRailInfo = new RailInfo(lastRailInfo.getX() + tileRect.width(), lastRailInfo.getY(), 0);
                break;
            case TrainDirection.UP:
                lastRailInfo = new RailInfo(lastRailInfo.getX(), lastRailInfo.getY() - tileRect.height(), 0);
                break;
            case TrainDirection.DOWN:
                lastRailInfo = new RailInfo(lastRailInfo.getX(), lastRailInfo.getY() + tileRect.height(), 0);
                break;
        }

        trainPath.addPoint(nextTilePoint, lastRailInfo);

        return trainPath;
    }

    private Point getNextTile(RailInfo[][] board, int row, int col, int direction) {
        Point nextTilePoint = getNextTilePoint(row, col, direction);

        row = nextTilePoint.x;
        col = nextTilePoint.y;

        if (row < 0 || row >= board.length || col < 0 || col >= board[0].length || board[row][col] == null) {
            return null;
        }

        return new Point(row, col);
    }

    private Point getNextTilePoint(int row, int col, int direction) {
        switch (direction) {
            case TrainDirection.LEFT:
                col--;
                break;
            case TrainDirection.RIGHT:
                col++;
                break;
            case TrainDirection.UP:
                row--;
                break;
            case TrainDirection.DOWN:
                row++;
                break;
        }

        return new Point(row, col);
    }

    private RailInfo[][] buildBoard(Rect tileRect, List<RailInfo> rails) {
        int tileWidth = tileRect.width();
        int tileHeight = tileRect.height();

        int rows = screenHeight / tileHeight;
        int cols = screenWidth / tileWidth;

        RailInfo[][] board = new RailInfo[rows][cols];
        for (RailInfo rail : rails) {
            int railRow = rail.getY() / tileHeight;
            int railCol = rail.getX() / tileWidth;
            board[railRow][railCol] = rail;
        }

        return board;
    }

    private int changeDirection(int trainHead, int rotation) {
        switch (trainHead) {
            case TrainDirection.RIGHT:
                switch (rotation) {
                    case RailInfo.HORIZONTAL:
                        return TrainDirection.RIGHT;
                    case RailInfo.BOTTOM_RIGHT_CORNER:
                        return TrainDirection.UP;
                    case RailInfo.TOP_RIGHT_CORNER:
                        return TrainDirection.DOWN;
                    case RailInfo.VERTICAL:
                    case RailInfo.TOP_LEFT_CORNER:
                    case RailInfo.BOTTOM_LEFT_CORNER:
                        return -1;
                }
            case TrainDirection.LEFT:
                switch (rotation) {
                    case RailInfo.HORIZONTAL:
                        return TrainDirection.LEFT;
                    case RailInfo.BOTTOM_LEFT_CORNER:
                        return TrainDirection.UP;
                    case RailInfo.TOP_LEFT_CORNER:
                        return TrainDirection.DOWN;
                    case RailInfo.VERTICAL:
                    case RailInfo.TOP_RIGHT_CORNER:
                    case RailInfo.BOTTOM_RIGHT_CORNER:
                        return -1;
                }
            case TrainDirection.UP:
                switch (rotation) {
                    case RailInfo.VERTICAL:
                        return TrainDirection.UP;
                    case RailInfo.TOP_RIGHT_CORNER:
                        return TrainDirection.LEFT;
                    case RailInfo.TOP_LEFT_CORNER:
                        return TrainDirection.RIGHT;
                    case RailInfo.HORIZONTAL:
                    case RailInfo.BOTTOM_RIGHT_CORNER:
                    case RailInfo.BOTTOM_LEFT_CORNER:
                        return -1;
                }
            case TrainDirection.DOWN:
                switch (rotation) {
                    case RailInfo.VERTICAL:
                        return TrainDirection.DOWN;
                    case RailInfo.BOTTOM_RIGHT_CORNER:
                        return TrainDirection.LEFT;
                    case RailInfo.BOTTOM_LEFT_CORNER:
                        return TrainDirection.RIGHT;
                    case RailInfo.HORIZONTAL:
                    case RailInfo.TOP_RIGHT_CORNER:
                    case RailInfo.TOP_LEFT_CORNER:
                        return -1;
                }
        }
        return -1;
    }
}
