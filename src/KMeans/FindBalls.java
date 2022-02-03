package KMeans;

import core.DImage;

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
    private ArrayList<Point> clusterList;
    public FindBalls(DImage img, int k){
        K = k;
        clusterList = new ArrayList(Collections.nCopies(K+1, 0));
        short[][] pixels = img.getBWPixelGrid();
        for (int r = 0; r < pixels.length; r++) {
            for (int c = 0; c < pixels[0].length; c++) {
                if (pixels[r][c] == 255) {
                    Datum pixel = new Datum(new Point(c, r), 255);
                    pixel.cluster = (int)(Math.random()*K+1);
                    dataList.add(pixel);
                }
            }
        }
    }
    public ArrayList<Point> findBalls(){
        ArrayList<Point> prevClusterList = (ArrayList<Point>) clusterList.clone();
        for (int i = 1; i <= K; i++) {
            Point totalCentroid = new Point(0,0);
            int datumInCluster = 0;
            for (Datum datum : dataList) {
                if (datum.cluster == i) {
                    totalCentroid.x += datum.pos.x;
                    totalCentroid.y += datum.pos.y;
                    datumInCluster++;
                }
            }
            Point meanCentroid = new Point(totalCentroid.x / datumInCluster,
                    totalCentroid.y / datumInCluster);
            clusterList.set(i, meanCentroid);
            //System.out.println("Cluster: " + K + ", x: " + meanCentroid.x + ", y: " + meanCentroid.y);
        }
        for (Datum datum : dataList) {
            ArrayList<ArrayList<Float>> alldistances = new ArrayList<>();
            for (int i = 1; i < clusterList.size(); i++) {
                alldistances.add(new ArrayList(Arrays.asList((float) i, dist(datum.pos.x, datum.pos.y, clusterList.get(i).x, clusterList.get(i).y))));
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
