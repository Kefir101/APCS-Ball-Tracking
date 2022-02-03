package Kmeans;

import processing.core.PVector;

import static processing.core.PApplet.dist;
import static processing.core.PApplet.hex;

public class Datum {
    Location pos;
    int color, cluster = 0;
    public Datum(Location pos_, int color_){
        this.pos = pos_;
        this.color = color_;
    }
    float distanceToMouse(float mouseX, float mouseY){
        return dist(mouseX, mouseY, pos.getx(), pos.gety());
    }
    @Override
    public String toString() {
        return (hex(this.color));
    }
}
