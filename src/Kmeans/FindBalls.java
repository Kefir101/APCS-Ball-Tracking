package Kmeans;

import Interfaces.PixelFilter;
import core.DImage;
import processing.core.PApplet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import static processing.core.PApplet.dist;

public class FindBalls/*extends PApplet /*implements PixelFilter*/ {
    private int numberOfDataPoints = 0;
    private int K;
    private boolean startClusering = false;
    private ArrayList<Datum> dataList = new ArrayList<>();
    private ArrayList<Location> clusterList;
    public FindBalls(DImage img, int k){
        K = k;
        clusterList = new ArrayList(Collections.nCopies(K+1, 0));
        short[][]pixels = img.getBWPixelGrid();
        for (int r = 0; r < pixels.length; r++) {
            for (int c = 0; c < pixels[0].length; c++) {
                if (pixels[r][c] == 255) {
                    Datum pixel = new Datum(new Location(c, r), 255);
                    pixel.cluster = (int)(Math.random()*K+1);
                    dataList.add(pixel);
                }
            }
        }
    }

    public ArrayList<Location> findBalls(){
        ArrayList<Location> prevClusterList = (ArrayList<Location>) clusterList.clone();
        for (int i = 1; i <= K; i++) {
            Location totalCentroid = new Location(0,0);
            int datumInCluster = 0;
            for (Datum datum : dataList) {
                if (datum.cluster == i) {
                    totalCentroid.setx(totalCentroid.getx() + datum.pos.getx());
                    totalCentroid.sety(totalCentroid.gety() + datum.pos.gety());
                    datumInCluster++;
                }
            }
            Location meanCentroid = new Location(totalCentroid.getx() / datumInCluster, totalCentroid.gety() / datumInCluster);
            clusterList.set(i, meanCentroid);
            //System.out.println("Cluster: " + K + ", x: " + meanCentroid.x + ", y: " + meanCentroid.y);
        }
        for (Datum datum : dataList) {
            ArrayList<ArrayList<Float>> alldistances = new ArrayList<>();
            for (int i = 1; i < clusterList.size(); i++) {
                alldistances.add(new ArrayList(Arrays.asList((float) i, dist(datum.pos.getx(), datum.pos.gety(), clusterList.get(i).getx(), clusterList.get(i).gety()))));
            }
            Collections.sort(alldistances, new Comparator<ArrayList<Float>>() {
                @Override
                public int compare(ArrayList<Float> a, ArrayList<Float> b) {
                    return a.get(1).compareTo(b.get(1));
                }
            });
            datum.cluster = (int) (alldistances.get(0).get(0)).floatValue();
        }
        if(clusterList.equals(prevClusterList)){
            System.out.println("DONE");
        }
        return clusterList;
    }


}
