package Filters;

import Interfaces.PixelFilter;
import KMeans.FindBallCenters;
import core.DImage;
import processing.core.PVector;

import java.util.ArrayList;

public class bestK implements PixelFilter {
    public static final int WHITE = 255, BLACK = 0;
    @Override
    public DImage processImage(DImage img) {
        DImage newImg = threshold(blur(img));
        /*short[][][] out = {img.getRedChannel(), img.getGreenChannel(), img.getBlueChannel()};
        int K = 6;
        ArrayList<PVector> balls;

         */
         /**compactness = is it an actual circle, check for size
         * isSeparated = are the crusters not too close to each other**/
         /*
        boolean isCompact = false;
        boolean isSeparated = false;
        do{
            FindBallCenters findBalls = new FindBallCenters(newImg, K);
            balls = findBalls.findBallCenters();
            boolean tooClose = false;
            boolean isLegit = true;
            for (int b = 1; b < balls.size(); b++) {
                PVector point = balls.get(b);
                if(!checkCluster(point, newImg)) {
                    balls.remove(b);
                    b--;
                    isLegit = false;
                }
            }
            for (int b1 = 1; b1 < balls.size(); b1++) {
                for (int b2 = b1 + 1; b2 < balls.size(); b2++) {
                    PVector a = balls.get(b1);
                    PVector b = balls.get(b2);
                    if (b1 != balls.size()-1) {
                        double dist = Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
                        if (dist < 80) {
                            tooClose = true;
                            //K--;
                        }
                    }
                }
            }
            if (!tooClose && isLegit) isSeparated = true;
            if (tooClose) K--;
        }while(!isSeparated && K < 6);

        /**DRAW THE RESULTED CRUSTERS*/
        /*
        int radius = 10;
        for (int b = 1; b < balls.size(); b++) {
            PVector point = balls.get(b);
            int x = (int) point.x;
            int y = (int) point.y;
            for (int i = -radius; i <= radius; i++) {
                for (int j = -radius; j <= radius; j++) {
                    int r = y + i;
                    int c = x + j;
                    if (isInBounds(img.getHeight(), img.getWidth(), r, c)) {
                        out[0][r][c] = 0;
                        out[1][r][c] = 0;
                        out[2][r][c] = 0;
                    }
                }
            }
        }

        img.setColorChannels(out[0],out[1], out[2]);
        return img;
        */
       return newImg;
    }
    private int findRadius(PVector center, DImage img){
        short[][]grid = img.getBWPixelGrid();
        /*int radUP = 1; int radDOWN = 1; int radR = 1; int radL = 1;
        int radiTotal = 0;
        while(grid[(int)center.y-radUP][(int)center.x] == 255 && center.y - radUP >= 0){//up
                radUP++;
                radiTotal++;
        }
        while(grid[(int)center.y+radDOWN][(int)center.x] == 255 && radDOWN+center.y < grid.length){//down
                radDOWN++;
                radiTotal++;
        }
        while(grid[(int)center.y][(int)center.x+radR] == 255 && radR + center.x < grid[0].length){//right
                radR++;
                radiTotal++;
        }
        while(grid[(int)center.y][(int)center.x-radL] == 255 && center.x- radL >= 0){//left
                radL++;
                radiTotal++;
        }
        double averageRad = radiTotal/4;
        if (averageRad-)

        return radiTotal/4;

         */
        int radius = 1;
        while(grid[(int)center.y-radius][(int)center.x] == 255 && center.y - radius >= 0){//up
            radius++;
        }
        return radius;
    }


    private boolean checkCluster(PVector point, DImage img) { //check for circleness and size
        short[][] BWgrid = img.getBWPixelGrid();
        int x = (int) point.x;
        int y = (int) point.y;
        double totalDist = 0;
        int numPoints = 0;
        int radius = findRadius(point, img); // find upper edge of cluster and return false if it is too big or too small
        if (radius < (img.getWidth() / 40.0) || radius > (img.getWidth() / 6.0)) return false;
        /** find total distances of all the points in the cluster**/
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                int Y = y + i;
                int X = x + j;
                if (isInBounds(img.getHeight(), img.getWidth(), Y, X))
                    if (BWgrid[Y][X] == 255) {
                        numPoints++;
                        double dist = Math.sqrt(((y - Y) * (y - Y) + (x - X) * (x - X)));
                        totalDist += dist;
                    }
            }
        }
        /**if the actual average distance-supposed average distance is less than 30 return true**/
        if (Math.abs((totalDist / numPoints) - (radius * 2 / 3.0)) < 30) {
            return true;
        }
        return false;
    }



    private DImage outline(DImage original, DImage img) {
        short[][][] out = {original.getRedChannel(), original.getGreenChannel(), original.getBlueChannel()};
        int maxX = 0, maxY = 0, minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        short[][] bwImg = img.getBWPixelGrid();
        for (int r = 0; r < img.getHeight(); r++) {
            for (int c = 0; c < img.getWidth(); c++) {
                if (bwImg[r][c] == WHITE) {
                    if (r > maxY) maxY = r;
                    if (r < minY) minY = r;
                    if (c > maxX) maxX= c;
                    if (c < minX) minX = c;
                }
            }
        }
        for (int i = minX; i <= maxX; i++) {
            out[0][minY][i] = WHITE;
            out[1][minY][i] = WHITE;
            out[2][minY][i] = BLACK;

            out[0][maxY][i] = WHITE;
            out[1][maxY][i] = WHITE;
            out[2][maxY][i] = BLACK;
        }
        for (int i = minY; i <= maxY; i++) {
            out[0][i][minX] = WHITE;
            out[1][i][minX] = WHITE;
            out[2][i][minX] = BLACK;

            out[0][i][maxX] = WHITE;
            out[1][i][maxX] = WHITE;
            out[2][i][maxX] = BLACK;
        }
        original.setColorChannels(out[0], out[1], out[2]);
        return original;
    }
    public DImage threshold(DImage img) {
        int height = img.getRedChannel().length;
        int width = img.getRedChannel()[0].length;
        hsv[][] pixels = convertToHSV(img);
        short[][] out = new short[height][width];
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                hsv color = pixels[r][c];
                if (color.value > 40 && color.saturation > 40) {
                    boolean red = color.hue >= 0 && color.hue <= 20;
                    /*boolean yellow = color.hue > 60 && color.hue <= 120;
                    boolean green = color.hue > 120 && color.hue <= 180;
                    boolean blue = color.hue > 180 && color.hue <= 300;

                     */

                    out[r][c] = WHITE;
                }
            }
        }
        DImage outImg = new DImage(width, height);
        outImg.setPixels(out);
        return outImg;
    }

    private hsv[][] convertToHSV(DImage img) {
        short[][][] in = {img.getRedChannel(), img.getGreenChannel(), img.getBlueChannel()};
        hsv[][] out = new hsv[img.getHeight()][img.getWidth()];
        for (int r = 0; r < img.getHeight(); r++) {
            for (int c = 0; c < img.getWidth(); c++) {
                hsv pixel = new hsv(in[0][r][c], in[1][r][c], in[2][r][c]);
                out[r][c] = pixel;
            }
        }
        return out;
    }

    public DImage blur(DImage in) {
        int height = in.getRedChannel().length;
        int width = in.getRedChannel()[0].length;
        short[][][] inColors = {in.getRedChannel(), in.getGreenChannel(), in.getBlueChannel()};
        short[][][] outColors = new short[3][height][width];
//        int kernelSize = Integer.parseInt(JOptionPane.showInputDialog(null, "Kernel Size (odd only): "));
        int kernelSize = 9;
        double[][] kernel = new double[kernelSize][kernelSize];
        createBoxBlur(kernel);
        double weightsSum = calculateSum(kernel);
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                if (!(r < kernelSize / 2 || c < kernelSize / 2 || r >= height - kernelSize / 2 || c >= width - kernelSize / 2)) {
                    outColors[0][r][c] = (short) computeOutputValue(r, c, inColors[0], kernel, weightsSum);
                    outColors[1][r][c] = (short) computeOutputValue(r, c, inColors[1], kernel, weightsSum);
                    outColors[2][r][c] = (short) computeOutputValue(r, c, inColors[2], kernel, weightsSum);
                }
            }
        }
        DImage out = new DImage(width, height);
        out.setColorChannels(outColors[0], outColors[1], outColors[2]);
        return out;
    }
    private void createBoxBlur(double[][] kernel) {
        for (int r = 0; r < kernel.length; r++) {
            for (int c = 0; c < kernel.length; c++) {
                kernel[r][c] = 1;
            }
        }
    }
    private boolean isInBounds(int height, int width, int r, int c){
        if (r < 0 || c < 0 || r >= height || c >= width) return false;
        return true;
    }
    private double computeOutputValue(int r, int c, short[][] pixels, double[][] kernel, double kernelSum) {
        double output = 0;
        int half = kernel.length / 2;
        for (int i = -half; i < half + 1; i++) {
            for (int j = -half; j < half + 1; j++) {
                output += pixels[r + i][c + j] * kernel[i + half][j + half];
            }
        }
        if (kernelSum != 0) output = output / kernelSum;
        if (output < BLACK) output = BLACK;
        if (output > WHITE) output = WHITE;
        return output;
    }
    private double calculateSum(double[][] kernel) {
        double total = 0;
        for (int r = 0; r < kernel.length; r++) {
            for (int c = 0; c < kernel[0].length; c++) {
                total += kernel[r][c];
            }
        }
        return total;
    }
    private void createGaussianBlur(double[][] kernel) {
        int half = kernel.length / 2;
        double stdv = 3;
        double a = (1 / (2 * Math.PI * stdv * stdv)) * Math.E;
        double b = (2 * stdv * stdv);
        for (int r = -half; r <= half; r++) {
            for (int c = -half; c <= half; c++) {
                double weight = Math.pow(a, -((r * r) + (c * c)) / b);
                kernel[r + half][c + half] = weight;
            }
        }
    }
}

