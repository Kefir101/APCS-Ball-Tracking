package KMeans;

import core.DImage;
import processing.core.PApplet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class FindBalls extends PApplet {
    private int K;
    private boolean startClustering;
    private ArrayList<Point> dataList = new ArrayList<>();
    ArrayList<Integer> clusterColorList = new ArrayList<>(Arrays.asList(0, color(255, 0, 0), color(0, 255, 0), color(0, 0, 255), color(255, 255, 0)));
    ArrayList<Point> clusterList = new ArrayList(Collections.nCopies(K+1, 0));
    public FindBalls(DImage img, int k){
        K = k;
        short[][] pixels = img.getBWPixelGrid();
        for (int r = 0; r < pixels.length; r++) {
            for (int c = 0; c < pixels[0].length; c++) {
                if (pixels[r][c] == 255) {
                    Point pixel = new Point(c, r);
                    pixel.cluster = (int)(Math.random()*K+1);
                    pixel.color = clusterColorList.get(pixel.cluster);
                    dataList.add(pixel);
                }
            }
        }
    }
    public ArrayList<Point> findBalls(){
        background(51);
        for (Point point : dataList) {
            point.color = clusterColorList.get(point.cluster);
            stroke(point.color);
            strokeWeight(8);
            point(point.x, point.y);
            if(point.cluster == 0){
                point.cluster = (int) (random(K) + 1);
            }
        }
        if(startClustering) {
            frameRate(1);
            ArrayList<Point> prevClusterList = (ArrayList<Point>) clusterList.clone();
            for (int i = 1; i <= K; i++) {
                Point totalCentroid = new Point(0, 0);
                int datumInCluster = 0;
                for (Point point : dataList) {
                    if (point.cluster == i) {
                        totalCentroid.x += point.x;
                        totalCentroid.y += point.y;
                        datumInCluster++;
                    }
                }
                Point meanCentroid = new Point(totalCentroid.x / datumInCluster, totalCentroid.y / datumInCluster);
                clusterList.set(i, meanCentroid);
                strokeWeight(0);
                fill(clusterColorList.get(i));
                //System.out.println("Cluster: " + K + ", x: " + meanCentroid.x + ", y: " + meanCentroid.y);
                ellipse(clusterList.get(i).x, clusterList.get(i).y, 20, 20);
            }
            for (Point point : dataList) {
                ArrayList<ArrayList<Float>> alldistances = new ArrayList<>();
                for (int i = 1; i < clusterList.size(); i++) {
                    alldistances.add(new ArrayList(Arrays.asList((float) i, dist(point.x, point.y, clusterList.get(i).x, clusterList.get(i).y))));
                }
                Collections.sort(alldistances, new Comparator<ArrayList<Float>>() {
                    @Override
                    public int compare(ArrayList<Float> a, ArrayList<Float> b) {
                        return a.get(1).compareTo(b.get(1));
                    }
                });
                point.cluster = (int) (alldistances.get(0).get(0)).floatValue();
            }
            if(clusterList.equals(prevClusterList)){
                System.out.println("DONE");
            }
        }else if (mousePressed){
            dataList.add(new Point(mouseX, mouseY));
        }
        return clusterList;
    }
    public void keyReleased() {
        if (key == 's') {
            noLoop();
            System.out.println("Stopped");
        } else if (key == 'a'){
            startClustering = true;
            System.out.println("startClustering");
        } else if(key == 'd'){
            loop();
            System.out.println("Started");
        }
    }
    public static void main(String[] args) {
        PApplet.main("FindBalls");
    }
}
