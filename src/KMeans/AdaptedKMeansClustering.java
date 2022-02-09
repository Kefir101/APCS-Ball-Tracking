package KMeans;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.*;

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
    ArrayList<PVector> clusterList = new ArrayList<>(Collections.nCopies(K+1, new PVector(0, 0)));
    ArrayList<PVector> whitePoints = new ArrayList<>();
    //main PApplet methods
    public void settings() {
        size((int) width, (int) height);
    }
    public void setup() {
        background(51);
        createData();
        initializeData();
        clusterList = findBestOriginalClusterList();
    }
    public void draw() {
        background(51);
        if(startClustering) {
            frameRate(1);
            drawData();
            for (Datum[] row : dataGrid) {
                for (Datum datum : row) {
                    if(datum.isData) closestClusterToPoint(datum);
                }
            }
            ArrayList<PVector> prevClusterList = (ArrayList<PVector>) clusterList.clone();
            strokeWeight(0);
            for (int i = 1; i <= K; i++) {
                stroke(clusterColorList.get(i));
                fill(clusterColorList.get(i));
                ellipse(clusterList.get(i).x, clusterList.get(i).y, 20, 20);
                recalculateCentroids(i);
            }
            if(clusterList.equals(prevClusterList)) {
                System.out.println("DONE");
            }
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

    //decomposed/helper methods
    public void createData(){
        int radius = 20;
        int[] centerXs = {100, 150, 200, 250};
        int[] centerYs = {100, 150, 200, 250};
        for (int cluster = 0; cluster < centerXs.length; cluster++) {
            int centerX = centerXs[cluster];
            int centerY = centerYs[cluster];
            for (int x = -radius+centerX; x <= radius+centerX; x++) {
                for (int y = -radius+centerY; y <= radius+centerY; y++) {
                    if(isInBounds(width, height, x, y)) img[y][x] = 255;
                }
            }
        }
    }
    public void initializeData(){
        for (int i = 0; i < dataGrid.length; i++) {
            for (int j = 0; j < dataGrid[0].length; j++) {
                Datum datum = new Datum(new PVector(j, i), img[i][j]);
                if(img[i][j] == 255) {
                    datum.isData = true;
                    whitePoints.add(new PVector(j, i));
                }
                dataGrid[i][j] = datum;
            }
        }
    }
    public ArrayList<PVector> findBestOriginalClusterList(){
        ArrayList<PVector> currentDistList = new ArrayList<>(Collections.nCopies(K+1, new PVector(0, 0)));
        ArrayList<PVector> maxDistList = null;
        double minMinDist = Double.MIN_VALUE;
        for (int i = 0; i < 100; i++) {
            double minDist = Double.MAX_VALUE;
            Collections.shuffle(whitePoints);
            for (int j = 1; j <= K; j++) currentDistList.set(j, whitePoints.get(j-1));
            for (int j = 1; j <= K; j++) {
                PVector a = currentDistList.get(j);
                for (int k = j+1; k <= K; k++) {
                    PVector b = currentDistList.get(k);
                    double dist = PVector.dist(a, b);
                    if(dist < minDist) minDist = dist;
                }
            }
            if(minDist > minMinDist) {
                maxDistList = (ArrayList<PVector>) currentDistList.clone();
                minMinDist = minDist;
            }
        }
        return maxDistList;
    }
    public void drawData(){
        for (Datum[] row : dataGrid) {
            for (Datum datum : row) {
                if(datum.isData){
                    if(datum.cluster != 0) datum.color = clusterColorList.get(datum.cluster);
                    stroke(datum.color);
                    point(datum.pos.x, datum.pos.y);
                }
            }
        }
    }
    public void closestClusterToPoint(Datum datum){
        ArrayList<ArrayList<Float>> allDistances = new ArrayList<>();
        for (int i = 1; i < clusterList.size(); i++) {
            float distance = dist(datum.pos.x, datum.pos.y, clusterList.get(i).x, clusterList.get(i).y);
            allDistances.add(new ArrayList<>(Arrays.asList((float) i, distance)));
        }
        allDistances.sort(Comparator.comparing(a -> a.get(1)));
        datum.cluster = (int) (allDistances.get(0).get(0)).floatValue();
    }
    public void recalculateCentroids(int i){
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
        PVector meanCentroid = new PVector((int) (totalCentroid.x / datumInCluster),
                (int) (totalCentroid.y / datumInCluster));
        clusterList.set(i, meanCentroid);
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
            whitePoints = new ArrayList<>();
            initializeData();
            clusterList = findBestOriginalClusterList();
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
