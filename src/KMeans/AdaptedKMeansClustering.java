package KMeans;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class AdaptedKMeansClustering extends PApplet {
    float width = 800;
    float height = 600;
    int K = 4;
    boolean startClustering = false;
    short[][] img = new short[(int) height][(int) width];
    Datum[][] dataGrid = new Datum[img.length][img[0].length];
    ArrayList<Integer> clusterColorList = new ArrayList<>(
            Arrays.asList(0, color(255, 0, 0), color(0, 255, 0), color(0, 0, 255),
                    color(255, 255, 0), color(255, 0, 255), color(0, 255, 255)));
    ArrayList<PVector> clusterList = new ArrayList(Collections.nCopies(K+1, 0));
    public void settings() {
        size((int) width, (int) height);
    }
    public void setup() {
        background(51);
        int radius = 10;
        int[] centerXs = {100, 300, 500, 500};
        int[] centerYs = {100, 300, 500, 200};
        for (int cluster = 0; cluster < centerXs.length; cluster++) {
            int centerX = centerXs[cluster];
            int centerY = centerYs[cluster];
            for (int x = -radius+centerX; x < radius+centerX; x++) {
                for (int y = -radius+centerY; y < radius+centerY; y++) {
                    if(isInBounds(width, height, x, y)) img[y][x] = 255;
                }
            }
        }
        for (int i = 0; i < dataGrid.length; i++) {
            for (int j = 0; j < dataGrid[0].length; j++) {
                Datum datum = new Datum(new PVector(j, i), img[i][j]);
                if(img[i][j] == 255) datum.cluster = (int) (random(K) + 1);
                dataGrid[i][j] = datum;
            }
        }
    }
    public void draw() {
        background(51);
        if(startClustering) {
            frameRate(1);
            strokeWeight(1);
            for (Datum[] row : dataGrid) {
                for (Datum datum : row) {
                    if(datum.color != 0){
                        datum.color = clusterColorList.get(datum.cluster);
                        stroke(datum.color);
                        point(datum.pos.x, datum.pos.y);
                    }
                }
            }
            ArrayList<PVector> prevClusterList = (ArrayList<PVector>) clusterList.clone();
            strokeWeight(0);
            for (int i = 1; i <= K; i++) {
                PVector totalCentroid = new PVector();
                int datumInCluster = 0;
                for (Datum[] row : dataGrid) {
                    for (Datum datum : row) {
                        if (datum.cluster == i) {
                            totalCentroid.x += datum.pos.x;
                            totalCentroid.y += datum.pos.y;
                            datumInCluster++;
                        }
                    }
                }
                PVector meanCentroid = new PVector(totalCentroid.x / datumInCluster, totalCentroid.y / datumInCluster);
                clusterList.set(i, meanCentroid);
                stroke(clusterColorList.get(i));
                fill(clusterColorList.get(i));
                ellipse(clusterList.get(i).x, clusterList.get(i).y, 20, 20);
            }
            for (Datum[] row : dataGrid) {
                for (Datum datum : row) {
                    if(datum.cluster != 0) {
                        ArrayList<ArrayList<Float>> allDistances = new ArrayList<>();
                        for (int i = 1; i < clusterList.size(); i++) {
                            float distance = dist(datum.pos.x, datum.pos.y, clusterList.get(i).x, clusterList.get(i).y);
                            allDistances.add(new ArrayList<>(Arrays.asList((float) i, distance)));
                        }
                        allDistances.sort(Comparator.comparing(a -> a.get(1)));
                        datum.cluster = (int) (allDistances.get(0).get(0)).floatValue();
                    }
                }
            }
            /** An attempt at deleting nearby clusters in order to remove overlap, but doesn't work. */
//            for (int i = 1; i < clusterList.size(); i++) {
//                for (int j = i+1; j < clusterList.size(); j++) {
//                    PVector a = clusterList.get(i);
//                    PVector b = clusterList.get(j);
//                    double dist = Math.sqrt((a.x - b.x)*(a.x - b.x) + (a.y - b.y)*(a.y - b.y));
//                    if(dist < 10){
//                        clusterList.remove(i);
//                        K--;
//                        i = 1;
//                        j = i+1;
//                    }
//                }
//            }
            if(clusterList.equals(prevClusterList)) System.out.println("DONE");
        }else {
            stroke(255);
            strokeWeight(8);
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if(img[i][j] == 255) point(j, i);
                }
            }
//            if (mousePressed) img[mouseY][mouseX] = 255;
        }
    }
    public void keyReleased() {
        if (key == 's') {
            noLoop();
            System.out.println("Stopped");
        } else if (key == 'a'){
            startClustering = true;
            System.out.println("startClustering");
        } else if (key == 'r'){
            frameRate(60);
            dataGrid = new Datum[img.length][img[0].length];
            for (int i = 0; i < dataGrid.length; i++) {
                for (int j = 0; j < dataGrid[0].length; j++) {
                    Datum datum = new Datum(new PVector(j, i), img[i][j]);
                    if(img[i][j] == 255) datum.cluster = (int) (random(K) + 1);
                    dataGrid[i][j] = datum;
                }
            }
            clusterList = new ArrayList(Collections.nCopies(K+1, 0));
            startClustering = false;
            System.out.println("reset");
        } else if(key == 'd'){
            loop();
            System.out.println("Started");
        }
    }
    private boolean isInBounds(float width, float height, int x, int y) {
        return x >= 0 && y >= 0 && !(x >= width) && !(y >= height);
    }
    public static void main(String[] args) {
        PApplet.main("KMeans.AdaptedKMeansClustering");
    }
}
