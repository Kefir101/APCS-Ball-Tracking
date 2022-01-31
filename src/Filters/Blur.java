package Filters;

import Interfaces.PixelFilter;
import core.DImage;

import javax.swing.*;

public class Blur implements PixelFilter {
    @Override
    public DImage processImage(DImage img) {
        short[][] red = img.getRedChannel();
        short[][] green = img.getGreenChannel();
        short[][] blue = img.getBlueChannel();
        short[][] redOut = img.getRedChannel();
        short[][] greenOut = img.getGreenChannel();
        short[][] blueOut = img.getBlueChannel();

        int kernelSize = Integer.parseInt(JOptionPane.showInputDialog(null, "what size kernel (odd only)"));

        double[][]kernel = new double[kernelSize][kernelSize];
        createGaussianBlur(kernel);
        double weightsSum = calculateSum(kernel);
        for (int r = 0; r < red.length; r++) {
            for (int c = 0; c < red[0].length; c++) {
                if (r < kernelSize/2 || c < kernelSize/2|| r >= red.length-kernelSize/2 || c >= red[0].length-kernelSize/2) {

                }else {
                    redOut[r][c] = (short)ComputeOutputValue(r, c, red, kernel, weightsSum);
                    greenOut[r][c] = (short)ComputeOutputValue(r, c, green, kernel, weightsSum);
                    blueOut[r][c] = (short)ComputeOutputValue(r, c, blue, kernel, weightsSum);
                }
            }
        }

        img.setColorChannels(redOut, greenOut, blueOut);
        return img;
    }

    private void createBoxBlur(double[][] kernel) {
        for (int r = 0; r < kernel.length; r++) {
            for (int c = 0; c < kernel.length; c++) {
                kernel[r][c] = 1;
            }
        }
    }

    private void createGaussianBlur(double[][] kernel){
        int half = kernel.length/2;
        double stdv = 3;
        for (int r = -half; r <= half; r++) {
            for (int c = -half; c <= half; c++) {
                double weight = Math.pow((1/(2*Math.PI*stdv*stdv))*Math.exp(1),-((r*r)+(c*c))/(2*stdv*stdv));
                kernel[r+half][c+half] = weight;
            }
        }
    }

    private double ComputeOutputValue(int r, int c, short[][] pixels, double[][] kernel, double kernelSum) {
        double output = 0;
        int half = kernel.length/2;
        for (int i = -half; i < half+1; i++) {
            for (int j = -half; j < half+1; j++) {
                output += pixels[r + i][c + j] * kernel[i + half][j + half];
            }
        }
        if (kernelSum != 0) {
            output =  output / kernelSum;
        }
        if(output < 0) output = 0;
        if(output > 255) output = 255;
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
}
