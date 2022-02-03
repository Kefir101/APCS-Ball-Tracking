package KMeans;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class KMeansClustering extends PApplet{
    float width = 800;
    float height = 800;
    int numberOfDataPoints = 0;
    int K = 4;
    boolean startClusering = false;
    ArrayList<Datum> dataList = randomDatumList(numberOfDataPoints, 255);
    ArrayList<Integer> clusterColorList = new ArrayList<>(Arrays.asList(0, color(255, 0, 0), color(0, 255, 0), color(0, 0, 255), color(255, 255, 0)));
    ArrayList<PVector> clusterList = new ArrayList(Collections.nCopies(K+1, 0));
    public void settings() {
        size((int) width, (int) height);
    }
    public void setup() {
        background(51);
        for (Datum datum : dataList) {
            datum.cluster = (int) (random(K) + 1);
        }
    }
    public void draw() {
        background(51);
        for (Datum datum : dataList) {
            datum.color = clusterColorList.get(datum.cluster);
            stroke(datum.color);
            strokeWeight(8);
            point(datum.pos.x, datum.pos.y);
            if(datum.cluster == 0){
                datum.cluster = (int) (random(K) + 1);
            }
        }
        if(startClusering) {
            frameRate(1);
            ArrayList<PVector> prevClusterList = (ArrayList<PVector>) clusterList.clone();
            for (int i = 1; i <= K; i++) {
                PVector totalCentroid = new PVector();
                int datumInCluster = 0;
                for (Datum datum : dataList) {
                    if (datum.cluster == i) {
                        totalCentroid.x += datum.pos.x;
                        totalCentroid.y += datum.pos.y;
                        datumInCluster++;
                    }
                }
                PVector meanCentroid = new PVector(totalCentroid.x / datumInCluster, totalCentroid.y / datumInCluster);
                clusterList.set(i, meanCentroid);
                strokeWeight(0);
                fill(clusterColorList.get(i));
                //System.out.println("Cluster: " + K + ", x: " + meanCentroid.x + ", y: " + meanCentroid.y);
                ellipse(clusterList.get(i).x, clusterList.get(i).y, 20, 20);
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
        }else if (mousePressed){
            dataList.add(new Datum(new Point(mouseX, mouseY), 255));
        }
    }
    public ArrayList<Datum> randomDatumList(int size, int dataColor) {
        ArrayList<Datum> list = new ArrayList();
        for (int i = 0; i < size; i++) {
            list.add(new Datum(new Point(random(width), random(height)), dataColor));
        }
        return list;
    }
    public void keyReleased() {
        if (key == 's') {
            noLoop();
            System.out.println("Stopped");
        } else if (key == 'a'){
            startClusering = true;
            System.out.println("startClustering");
        } else if (key == 'r'){
            frameRate(60);
            dataList = randomDatumList(numberOfDataPoints, 255);
            clusterList = new ArrayList(Collections.nCopies(K+1, 0));
            startClusering = false;
            System.out.println("reset");
        } else if(key == 'd'){
            loop();
            System.out.println("Started");
        }
    }
    public static void main(String[] args) {
        PApplet.main("KMeans.KMeansClustering");
    }
}
