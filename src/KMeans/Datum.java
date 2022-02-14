package KMeans;

import processing.core.PVector;

import static processing.core.PApplet.dist;
import static processing.core.PApplet.hex;

public class Datum {
    PVector pos;
    public int color, cluster = 0;
    boolean isData = false;
    public Datum(PVector pos_, int color_){
        this.pos = pos_;
        this.color = color_;
    }
    public float distanceToMouse(float mouseX, float mouseY){
        return dist(mouseX, mouseY, pos.x, pos.y);
    }
    @Override
    public String toString() {
        return (hex(this.color));
    }
}
