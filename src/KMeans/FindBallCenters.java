package KMeans;

import core.DImage;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.*;

public class FindBallCenters {
    int K;
    ArrayList<PVector> clusterList;
    public ArrayList<Datum> whitePoints;
    int iterations;
    public FindBallCenters(DImage img, int k){
        K = k;
        clusterList = new ArrayList(Collections.nCopies(K+1, new PVector(0, 0)));
        whitePoints = new ArrayList<>();
        short[][] pixels = img.getBWPixelGrid();
        initializeData(pixels);
        clusterList = findBestOriginalClusterList();
        iterations = 0;
//        System.out.println("SETUP COMPLETE");
    }
    public ArrayList<PVector> findBallCenters(){
        ArrayList<PVector> prevClusterList;
        do {
//            System.out.println("doing closest C to P");
            for(Datum datum : whitePoints) closestClusterToPoint(datum);
//            System.out.println("finished closest C to P");
            prevClusterList = (ArrayList<PVector>) clusterList.clone();
            for (int i = 1; i <= K; i++) recalculateCentroids(i);
//            System.out.println("finished recalculating Cs");
            iterations++;
            if(iterations == 23) break;
        } while (!clusterList.equals(prevClusterList));
        return clusterList;
    }

    //decomposed/helper methods
    public void initializeData(short[][] pixels){
        //go through all the pixels, and if the point is white add it to the arraylist
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
        //for X iterations (X being 100), try and find the maximum (minimum distance) between the K clusters
        //meaning: where all the clusters are farthest away
        for (int i = 0; i < 100; i++) {
            //shuffle the points and choose the first K ones
            double minDist = Double.MAX_VALUE;
            Collections.shuffle(tempWhitePoints);
            for (int j = 1; j <= K; j++) currentDistList.set(j, tempWhitePoints.get(j-1).pos);
            //find the minimum distance between all those clusters/points
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
        //go through all the clusters, keep track of the minimum distance and cluster
        int cluster = 1;
        double minDist = Double.MAX_VALUE;
        for (int i = 1; i < clusterList.size(); i++) {
            double distance = Math.sqrt((datum.pos.x - clusterList.get(i).x)*(datum.pos.x - clusterList.get(i).x)
                    + (datum.pos.y - clusterList.get(i).y)*(datum.pos.y - clusterList.get(i).y));
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
        //find the total and then mean of position all the points from each cluster
        for(Datum datum : whitePoints){
            if (datum.cluster == i) {
                totalCentroid.x += datum.pos.x;
                totalCentroid.y += datum.pos.y;
                datumInCluster++;
            }
        }
        PVector meanCentroid = new PVector((int) (totalCentroid.x / datumInCluster),
                (int) (totalCentroid.y / datumInCluster));
        //set that cluster to the mean
        clusterList.set(i, meanCentroid);
    }

    public static void main(String[] args) {
        PApplet.main("KMeans.FindBallCenters");
    }
}
