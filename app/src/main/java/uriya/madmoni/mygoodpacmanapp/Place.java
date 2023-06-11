package uriya.madmoni.mygoodpacmanapp;

import android.graphics.Canvas;
import android.util.Log;

public class Place {
    private int x;
    private int y;

    public Place(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Place() {

    }

    public boolean isSame(int x, int y) {
        return this.x == x && this.y == y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getTotal() {
        return x+y;
    }

}
