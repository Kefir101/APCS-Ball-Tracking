package Filters;

import Interfaces.PixelFilter;
import core.DImage;

public class BlurAndThreshold implements PixelFilter {
    @Override
    public DImage processImage(DImage img) {
        return threshold(blur(img));
    }
    private DImage outline(DImage original, DImage img) {
        short[][][] out = {original.getRedChannel(), original.getGreenChannel(), original.getBlueChannel()};
        int maxX = 0, maxY = 0, minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        short[][] bwImg = img.getBWPixelGrid();
        System.out.println(out[0].length + " " + bwImg.length);
        System.out.println(out[0][0].length + " " + bwImg[0].length);
        for (int r = 0; r < img.getHeight(); r++) {
            for (int c = 0; c < img.getWidth(); c++) {
                if (bwImg[r][c] == 255) {
                    if (r > maxY) maxY = r;
                    if (r < minY) minY = r;
                    if (c > maxX) maxX= c;
                    if (c < minX) minX = c;
                }
            }
        }
        for (int i = minX; i <= maxX; i++) {
            out[0][minY][i] = 255;
            out[1][minY][i] = 255;
            out[2][minY][i] = 0;

            out[0][maxY][i] = 255;
            out[1][maxY][i] = 255;
            out[2][maxY][i] = 0;
        }
        for (int i = minY; i <= maxY; i++) {
            out[0][i][minX] = 255;
            out[1][i][minX] = 255;
            out[2][i][minX] = 0;

            out[0][i][maxX] = 255;
            out[1][i][maxX] = 255;
            out[2][i][maxX] = 0;
        }
        original.setColorChannels(out[0], out[1], out[2]);
        return original;
    }
    public DImage threshold(DImage img) {
        int height = img.getRedChannel().length;
        int width = img.getRedChannel()[0].length;
        short[][][] in = {img.getRedChannel(), img.getGreenChannel(), img.getBlueChannel()};
        short[][] out = new short[height][width];
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                int[] rgb = {in[0][r][c], in[1][r][c], in[2][r][c]};
                boolean red = rgb[0] > 2.2 * rgb[1] && rgb[0] > 2.2 * rgb[2];
                boolean orange = rgb[0] > 2.2 * rgb[2] && rgb[1] > 2.2 * rgb[2] && rgb[0] > rgb[1]*1.3;
                boolean yellow = rgb[0] > 1.9 * rgb[2] && rgb[1] > 1.9 * rgb[2];
                boolean green = rgb[1] > 1.5 * rgb[0] && rgb[1] > 1.5 * rgb[2];
                boolean blue = rgb[2] > 1.5 * rgb[0] && rgb[2] > 1.5 * rgb[1];
                if (red || orange || yellow || green || blue) out[r][c] = 255;
            }
        }
        DImage outImg = new DImage(width, height);
        outImg.setPixels(out);
        return outImg;
    }
    public DImage blur(DImage in) {
        int height = in.getRedChannel().length;
        int width = in.getRedChannel()[0].length;
        short[][][] inColors = {in.getRedChannel(), in.getGreenChannel(), in.getBlueChannel()};
        short[][][] outColors = new short[3][height][width];
//        int kernelSize = Integer.parseInt(JOptionPane.showInputDialog(null, "Kernel Size (odd only): "));
        int kernelSize = 15;
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
    private double computeOutputValue(int r, int c, short[][] pixels, double[][] kernel, double kernelSum) {
        double output = 0;
        int half = kernel.length / 2;
        for (int i = -half; i < half + 1; i++) {
            for (int j = -half; j < half + 1; j++) {
                output += pixels[r + i][c + j] * kernel[i + half][j + half];
            }
        }
        if (kernelSum != 0) output = output / kernelSum;
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

