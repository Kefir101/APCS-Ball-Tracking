package Filters;

import Interfaces.PixelFilter;
import KMeans.Datum;
import KMeans.FindBallCenters;
import core.DImage;
import processing.core.PVector;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class FilterAndBestK implements PixelFilter {
    public static final int WHITE = 255, BLACK = 0;
    @Override
    public DImage processImage(DImage img) {
        DImage BWImg = threshold(blur(img));
        short[][][] out = {img.getRedChannel(), img.getGreenChannel(), img.getBlueChannel()};
        int K = 6;
        FindBallCenters findBallCenters;
        ArrayList<PVector> balls;
        boolean keepGoing;
        double proximity = Math.sqrt(img.getWidth()*img.getWidth() + img.getHeight()* img.getHeight()) / 8;
        System.out.println(proximity);
        do{
            keepGoing = false;
            boolean tooClose = false, notBall = false;
            findBallCenters = new FindBallCenters(BWImg, K);
            balls = findBallCenters.findBallCenters();
            for (int b1 = 1; b1 < balls.size(); b1++) {
                for (int b2 = b1 + 1; b2 < balls.size(); b2++) {
                    PVector a = balls.get(b1);
                    PVector b = balls.get(b2);
//                    if (b1 != balls.size()-1) {
                        double dist = Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
                        if (dist < proximity) {
                            tooClose = true;
                        }

                }
            }
//            for (int b = 1; b < balls.size(); b++) {
//                PVector point = balls.get(b);
                if(!checkCluster(balls, BWImg, findBallCenters.whitePoints)) {
                    notBall = true;
                }
           // }
            if(K == 0) break;
            if (tooClose || notBall){
                keepGoing = true;
                K--;
                System.out.println("K = " + (K+1) + " -> K = " + K);
            }
        }while(keepGoing);
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
        writeBallCentersToFile(balls);
        return img;
    }
    private boolean checkCluster(ArrayList<PVector> balls, DImage img, ArrayList<KMeans.Datum> whitePoints) { //check for circleness and size
        double totalDist = 0;
        int numPoints = 0;
        for (int i = 1; i < balls.size() ; i++) {
            PVector center = balls.get(i);
            for (Datum point : whitePoints) {
                if (point.cluster == i) {
                    totalDist += Math.sqrt((center.x - point.getPos().x) * (center.x - point.getPos().x) +
                            (center.y - point.getPos().y) * (center.y - point.getPos().y));
                    numPoints++;
                }
            }
            double averageRadius = totalDist/numPoints;
            totalDist = 0;
            numPoints = 0;
            double radius = averageRadius * 3.2 / 2;
            System.out.println("X: " + center.x + ", Y: " + center.y + ", R: " + radius);
            System.out.println("width" + img.getWidth());
            if (radius < (img.getWidth() / 9.0) || radius > (img.getWidth() / 4.0)) return false;
        }
        return true;
    }
    public DImage threshold(DImage img) {
        int height = img.getRedChannel().length;
        int width = img.getRedChannel()[0].length;
        HSV[][] pixels = RGBToHSV(img);
        short[][] out = new short[height][width];
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                HSV color = pixels[r][c];
                double H = color.h, S = color.s, V = color.v;
                boolean red = (H > 340 || H < 16) && S > 70 && V > 70;
                boolean orange = H > 15 && H < 35 && S > 55 && V > 65;
                boolean yellow = H > 35 && H < 55 && S > 20 && V > 70;
                boolean green = H > 95 && H < 190 && S > 30 && V > 40;
                boolean blue = H > 200 && H < 230 && S > 40 && V > 40;
                boolean purple = H > 250 && H < 310 && S > 45 && V > 35;
                if (red || orange || yellow || green || blue || purple) out[r][c] = WHITE;
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

    private void writeBallCentersToFile(ArrayList<PVector> balls){
        try(FileWriter fw = new FileWriter("ballCenters.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter writer = new PrintWriter(bw))
        {
            for (int i = 1; i < balls.size(); i++) {
                PVector ball = balls.get(i);
                writer.write("Ball" + i + " X: " + ball.x + ", Y: " + ball.y + "; ");
            }
            writer.write("\n");
            System.out.println("Center positions written to ballCenters.txt");
        } catch (IOException e) {
            System.out.println("Error in writing centers to ballCenters.txt");
        }
    }
}