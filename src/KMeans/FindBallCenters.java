package KMeans;

import core.DImage;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.*;

public class FindBallCenters {
    int K;
    ArrayList<PVector> clusterList;
    ArrayList<Datum> whitePoints;
    int iterations;
    public FindBallCenters(DImage img, int k){
        K = k;
        clusterList = new ArrayList(Collections.nCopies(K+1, new PVector(0, 0)));
        whitePoints = new ArrayList<>();
        short[][] pixels = img.getBWPixelGrid();
        initializeData(pixels);
        clusterList = findBestOriginalClusterList();
        iterations = 0;
        System.out.println("SETUP COMPLETE");
    }
    public ArrayList<PVector> findBallCenters(){
        ArrayList<PVector> prevClusterList;
        do {
            System.out.println("doing closest C to P");
            for(Datum datum : whitePoints) closestClusterToPoint(datum);
            System.out.println("finished closest C to P");
            prevClusterList = (ArrayList<PVector>) clusterList.clone();
            for (int i = 1; i <= K; i++) recalculateCentroids(i);
            System.out.println("finished recalculating Cs");
            iterations++;
        } while (!clusterList.equals(prevClusterList));
        System.out.println(iterations);
        return clusterList;
    }

    //decomposed/helper methods
    public void initializeData(short[][] pixels){
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[0].length; j++) {
                if(pixels[i][j] == 255) {
                    whitePoints.add(new Datum(new PVector(j, i), 255));
                }
            }
        }
    }
    public ArrayList<PVector> findBestOriginalClusterList(){
        ArrayList<Datum> tempWhitePoints = (ArrayList<Datum>) whitePoints.clone();
        ArrayList<PVector> currentDistList = new ArrayList<>(Collections.nCopies(K+1, new PVector(0, 0)));
        ArrayList<PVector> maxDistList = null;
        double minMinDist = Double.MIN_VALUE;
        for (int i = 0; i < 100; i++) {
            double minDist = Double.MAX_VALUE;
            Collections.shuffle(tempWhitePoints);
            for (int j = 1; j <= K; j++) currentDistList.set(j, tempWhitePoints.get(j-1).pos);
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
        int cluster = 1;
        double minDist = Double.MAX_VALUE;
        for (int i = 1; i < clusterList.size(); i++) {
            double distance = Math.hypot(datum.pos.x - clusterList.get(i).x, datum.pos.y - clusterList.get(i).y);
            if(distance < minDist) {
                cluster = i;
                minDist = distance;
            }
        }
        datum.cluster = cluster;
    }
    public void recalculateCentroids(int i){
        PVector totalCentroid = new PVector();
        int datumInCluster = 0;
        for(Datum datum : whitePoints){
            if (datum.cluster == i) {
                totalCentroid.x += datum.pos.x;
                totalCentroid.y += datum.pos.y;
                datumInCluster++;
            }
        }
        PVector meanCentroid = new PVector((int) (totalCentroid.x / datumInCluster),
                (int) (totalCentroid.y / datumInCluster));
        clusterList.set(i, meanCentroid);
    }

    public static void main(String[] args) {
        PApplet.main("KMeans.FindBallCenters");
    }
}
