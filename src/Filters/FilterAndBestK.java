package Filters;

import Interfaces.PixelFilter;
import KMeans.FindBallCenters;
import core.DImage;
import processing.core.PVector;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FilterAndBestK implements PixelFilter {
    public static final int WHITE = 255, BLACK = 0;
    @Override
    public DImage processImage(DImage img) {
        DImage newImg = threshold(blur(img));
        short[][][] out = {img.getRedChannel(), img.getGreenChannel(), img.getBlueChannel()};
        int K = 6;
        ArrayList<PVector> balls;
        boolean keepGoing;
        do{
            keepGoing = false;
            boolean tooClose = false, notBall = false;
            balls = new FindBallCenters(newImg, K).findBallCenters();
            for (int b1 = 1; b1 < balls.size(); b1++) {
                for (int b2 = b1 + 1; b2 < balls.size(); b2++) {
                    PVector a = balls.get(b1);
                    PVector b = balls.get(b2);
                    if (b1 != balls.size()-1) {
                        double dist = Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
                        if (dist < 110) {
                            tooClose = true;
                        }
                    }
                }
            }
            for (int b = 1; b < balls.size(); b++) {
                PVector point = balls.get(b);
                if(!checkCluster(point, newImg)) {
                   notBall = true;
                }
            }
            if(K == 0) break;
            if (tooClose || notBall){
                keepGoing = true;
                K--;
                System.out.println("K = " + (K+1) + " -> K = " + K);
            }
        }while(keepGoing);
        if (K == 0) {
            K = 6;
            balls = new FindBallCenters(newImg, K).findBallCenters();
        }

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
        try {
            FileWriter writer = new FileWriter("center_positions.txt");
            for (int i = 1; i < balls.size(); i++) {
                PVector ball = balls.get(i);
                writer.write(ball.x + ", " + ball.y + "\n");
            }
            writer.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return img;
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
        return Math.abs((totalDist / numPoints) - (radius * 2 / 3.0)) < 30;
    }
    public DImage threshold(DImage img) {
        int height = img.getRedChannel().length;
        int width = img.getRedChannel()[0].length;
        HSV[][] pixels = RGBToHSV(img);
        short[][] out = new short[height][width];
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                HSV color = pixels[r][c];
                if (color.value > 40 && color.saturation > 40) {
                    out[r][c] = WHITE;
                }
            }
        }
        DImage outImg = new DImage(width, height);
        outImg.setPixels(out);
        return outImg;
    }
    private HSV[][] RGBToHSV(DImage img) {
        short[][][] in = {img.getRedChannel(), img.getGreenChannel(), img.getBlueChannel()};
        HSV[][] out = new HSV[img.getHeight()][img.getWidth()];
        for (int r = 0; r < img.getHeight(); r++) {
            for (int c = 0; c < img.getWidth(); c++) {
                HSV pixel = new HSV(in[0][r][c], in[1][r][c], in[2][r][c]);
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
                if (!(r < kernelSize / 2 || c < kernelSize / 2
                        || r >= height - kernelSize / 2 || c >= width - kernelSize / 2)) {
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
        for (int i = -half; i <= half; i++) {
            for (int j = -half; j <= half; j++) {
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
        for (double[] doubles : kernel) {
            for (double val : doubles) {
                total += val;
            }
        }
        return total;
    }
}

