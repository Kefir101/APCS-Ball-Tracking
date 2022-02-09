package Filters;

public class HSV {
    /**hue: 0-255 saturation: 0-100 value: 0-100**/
    double hue, saturation, value;
    public HSV(double r, double g, double b){
        r = r/255.0;
        g = g/255.0;
        b = b/255.0;
        double cMax = Math.max(r, Math.max(g, b));
        double cMin = Math.min(r, Math.min(g, b));
        double diff = cMax - cMin;
        hue = saturation = value = -1;
        if(cMax == cMin) hue = 0;
        else if (cMax == r){
            hue = (60*((g-b)/diff)+360) % 360;
        } else if (cMax == g){
            hue = (60*((b-r)/diff)+120) % 360;
        } else if (cMax == b){
            hue = (60*((r-g)/diff)+240) % 360;
        }
        if (cMax == 0) saturation = 0;
        else saturation = diff/cMax * 100;
        value = cMax * 100;
    }
}
