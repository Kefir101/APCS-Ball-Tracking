package Filters;

public class HSV {
    /**
     * Hue: 0 to 255
     * Saturation: 0 to 100
     * Value: 0 to 100
     **/
    double h, s, v;

    public HSV(double r, double g, double b) {
        r = r / 255.0;
        g = g / 255.0;
        b = b / 255.0;
        double cMax = Math.max(r, Math.max(g, b));
        double cMin = Math.min(r, Math.min(g, b));
        double diff = cMax - cMin;
        if (cMax == cMin) h = 0;
        else if (cMax == r) h = (60 * ((g - b) / diff) + 360) % 360;
        else if (cMax == g) h = (60 * ((b - r) / diff) + 120) % 360;
        else if (cMax == b) h = (60 * ((r - g) / diff) + 240) % 360;
        s = ((cMax == 0) ? 0 : diff / cMax * 100);
        v = cMax * 100;
    }
}