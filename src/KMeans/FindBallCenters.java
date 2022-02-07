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
    static final int closeEnough = 10;
    Datum[][] dataGrid;
    ArrayList<Integer> clusterColorList = new ArrayList<>(
            Arrays.asList(0, color(255, 0, 0), color(0, 255, 0), color(0, 0, 255),
                    color(255, 255, 0), color(255, 0, 255), color(0, 255, 255)));
    ArrayList<PVector> clusterList;
    public FindBallCenters(DImage img, int k){
        K = k;
        clusterList = new ArrayList(Collections.nCopies(K+1, 0));
        short[][] pixels = img.getBWPixelGrid();
        dataGrid = new Datum[pixels.length][pixels[0].length];
        for (int i = 0; i < dataGrid.length; i++) {
            for (int j = 0; j < dataGrid[0].length; j++) {
                Datum datum = new Datum(new PVector(j, i), pixels[i][j]);
                if(pixels[i][j] == 255) datum.cluster = (int) (random(K) + 1);
                dataGrid[i][j] = datum;
            }
        }
    }
    public ArrayList<PVector> findBallCenters(){
        ArrayList<PVector> prevClusterList = null;
        boolean noChange = false;
        while(!noChange) {
            for (Datum[] row : dataGrid) {
                for (Datum datum : row) {
                    if (datum.color != 0) {
                        datum.color = clusterColorList.get(datum.cluster);
                    }
                }
            }
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
            }
            for (Datum[] row : dataGrid) {
                for (Datum datum : row) {
                    if (datum.cluster != 0) {
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
            /*
           if(prevClusterList != null) {
                noChange = true;
                for (int i = 1; i < clusterList.size(); i++) {
                    PVector oldList = prevClusterList.get(i);
                    PVector newList = clusterList.get(i);
                    float distance = dist(oldList.x, oldList.y, newList.x, newList.y);
                    if (distance > closeEnough) noChange = false;
                }
            }

             */


            if(prevClusterList!=null&& prevClusterList.equals(clusterList)) {
                break;
            }
            prevClusterList = (ArrayList<PVector>) clusterList.clone();
//            System.out.println(clusterList);
        }
        return clusterList;
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
