package KMeans;

import core.DImage;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class FindBallCenters extends PApplet {
    int K;
    boolean startClustering = false;
    Datum[][] dataGrid;
    ArrayList<PVector> clusterList;
    ArrayList<PVector> whitePoints;
    public FindBallCenters(DImage img, int k){
        K = k;
        clusterList = new ArrayList(Collections.nCopies(K+1, new PVector(0, 0)));
        whitePoints = new ArrayList<>();
        short[][] pixels = img.getBWPixelGrid();
        dataGrid = new Datum[pixels.length][pixels[0].length];
        initializeData(pixels);
        clusterList = findBestOriginalClusterList();
    }
    public ArrayList<PVector> findBallCenters(){
        ArrayList<PVector> prevClusterList;
        do {
            for (Datum[] row : dataGrid) {
                for (Datum datum : row) {
                    if(datum.isData) closestClusterToPoint(datum);
                }
            }
            prevClusterList = (ArrayList<PVector>) clusterList.clone();
            for (int i = 1; i <= K; i++) {
                recalculateCentroids(i);
            }
        } while (!clusterList.equals(prevClusterList));
        return clusterList;
    }

    //decomposed/helper methods
    public void initializeData(short[][] pixels){
        for (int i = 0; i < dataGrid.length; i++) {
            for (int j = 0; j < dataGrid[0].length; j++) {
                Datum datum = new Datum(new PVector(j, i), pixels[i][j]);
                if(pixels[i][j] == 255) {
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
        if (key == 'a'){
            startClustering = true;
            System.out.println("Started to Cluster");
        }
    }
    public static void main(String[] args) {
        PApplet.main("KMeans.FindBallCenters");
    }
}
