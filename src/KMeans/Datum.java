package KMeans;

import static processing.core.PApplet.dist;
import static processing.core.PApplet.hex;

public class Datum {
    Point pos;
    int color, cluster = 0;
    public Datum(Point pos_, int color_){
        this.pos = pos_;
        this.color = color_;
    }
    float distanceToMouse(float mouseX, float mouseY){
        return dist(mouseX, mouseY, pos.x, pos.y);
    }
    @Override
    public String toString() {
        return (hex(this.color));
    }
}
