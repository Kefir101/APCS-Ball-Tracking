package Filters;

import Interfaces.PixelFilter;
import core.DImage;

import javax.swing.*;

public class BlurAndThreshold implements PixelFilter {
    @Override
    public DImage processImage(DImage img) {
        return threshold(blur(img));
    }
    public DImage threshold(DImage img) {
        int height = img.getRedChannel().length;
        int width = img.getRedChannel()[0].length;
        short[][][] in = {img.getRedChannel(), img.getGreenChannel(), img.getBlueChannel()};
        short[][] out = new short[height][width];
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                int red = in[0][r][c];
                int green = in[1][r][c];
                int blue = in[2][r][c];
                if (red > 2 * green && red > 2 * blue) out[r][c] = 255;
            }
        }
        DImage outImg = new DImage(width, height);
        outImg.setPixels(out);
        return outImg;
    }
    public DImage blur(DImage img) {
        int height = img.getRedChannel().length;
        int width = img.getRedChannel()[0].length;
        short[][][] in = {img.getRedChannel(), img.getGreenChannel(), img.getBlueChannel()};
        short[][][] out = new short[3][height][width];

        int kernelSize = Integer.parseInt(JOptionPane.showInputDialog(null, "Kernel Size (odd only): "));
        double[][] kernel = new double[kernelSize][kernelSize];
        createBoxBlur(kernel);
        double weightsSum = calculateSum(kernel);
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                if (!(r < kernelSize / 2 || c < kernelSize / 2 || r >= height - kernelSize / 2 || c >= width - kernelSize / 2)) {
                    out[0][r][c] = (short) ComputeOutputValue(r, c, in[0], kernel, weightsSum);
                    out[1][r][c] = (short) ComputeOutputValue(r, c, in[1], kernel, weightsSum);
                    out[2][r][c] = (short) ComputeOutputValue(r, c, in[2], kernel, weightsSum);
                }
            }
        }
        img.setColorChannels(out[0], out[1], out[2]);
        return img;
    }
    private void createBoxBlur(double[][] kernel) {
        for (int r = 0; r < kernel.length; r++) {
            for (int c = 0; c < kernel.length; c++) {
                kernel[r][c] = 1;
            }
        }
    }
    private double ComputeOutputValue(int r, int c, short[][] pixels, double[][] kernel, double kernelSum) {
        double output = 0;
        int half = kernel.length / 2;
        for (int i = -half; i < half + 1; i++) {
            for (int j = -half; j < half + 1; j++) {
                output += pixels[r + i][c + j] * kernel[i + half][j + half];
            }
        }
        if (kernelSum != 0) {
            output = output / kernelSum;
        }
        if (output < 0) output = 0;
        if (output > 255) output = 255;
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
        for (int r = -half; r <= half; r++) {
            for (int c = -half; c <= half; c++) {
                double weight = Math.pow((1 / (2 * Math.PI * stdv * stdv)) * Math.exp(1), -((r * r) + (c * c)) / (2 * stdv * stdv));
                kernel[r + half][c + half] = weight;
            }
        }
    }
}

