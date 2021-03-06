package com.example.darlokh.test_smartwatch;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class LandmarkContainer {
    private ArrayList<Landmark> lmArr = new ArrayList<Landmark>();
    private Landmark myLocation;
    private Landmark targetLocation;
    private String TAG = "lmContainer";
    private int watchDisplayWidth = 280;
    private double factor = 1;

    public LandmarkContainer(){}

    public void addLandmark(Landmark newLandmark){
        lmArr.add(newLandmark);
    }

    public void distanceLandmarksToMyLocation(){
        for(int i=0; i < lmArr.size(); i++){
            lmArr.get(i).euclideanDist(myLocation);
        }
        targetLocation.euclideanDist(myLocation);
    }

    public void clearLmArray(){
        lmArr = new ArrayList<Landmark>();
    }

    public void sortByDistance(){
        Collections.sort(lmArr, new Comparator<Landmark>() {
            @Override
            public int compare(Landmark o1, Landmark o2) {
                double compResult = (o1.dist - o2.dist);
                if(compResult > 0){
                    return 1;
                } else if (compResult < 0) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
    }

    public JSONObject myLocationToJSONObject(){
        JSONObject tmp = new JSONObject();
        try {
            tmp.put("x", myLocation.x);
            tmp.put("y", myLocation.y);
            tmp.put("tag", myLocation.tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tmp;
    }
    public JSONObject myTargetToJSONObject(){
        JSONObject tmp = new JSONObject();
        try {
            tmp.put("x", targetLocation.x);
            tmp.put("y", targetLocation.y);
            tmp.put("tag", targetLocation.tag);
            tmp.put("dist", targetLocation.dist);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tmp;
    }
    private JSONObject factorToJSONObject(){
        JSONObject tmp = new JSONObject();
        try {
            tmp.put("factor", factor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tmp;
    }

    // builds a JSONObject such that myLocation is at index 0, targetLocation at index 1 and
    // landmarks from OSM are added sorted by 'dist' (euclidean distance to myLocation)
    public JSONArray containerToJSONObject(){
        JSONObject tmpLocation = myLocationToJSONObject();
        JSONObject tmpTarget = myTargetToJSONObject();
        JSONObject tmpFactor = factorToJSONObject();
        JSONArray tmpArr = new JSONArray();
        tmpArr.put(tmpLocation);
        tmpArr.put(tmpTarget);
        tmpArr.put(tmpFactor);
        sortByDistance();

        for(int i=0; i < lmArr.size(); i++){
            JSONObject tmp = new JSONObject();
            try {
                tmp.put("x", lmArr.get(i).x);
                tmp.put("y", lmArr.get(i).y);
                tmp.put("tag", lmArr.get(i).tag);
                tmp.put("dist", lmArr.get(i).dist);
                tmpArr.put(tmp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tmpArr;
    }

    public ArrayList<Double> getMinMaxCoords(){
        double tmpMaxLat = -99999;
        double tmpMinLat = 99999;
        double tmpMaxLon = -99999;
        double tmpMinLon = 99999;
        for (int i=0; i<lmArr.size(); i++){
            // landmark(lon, lat)
            tmpMaxLon = Math.max(lmArr.get(i).x, tmpMaxLon);
            tmpMinLon = Math.min(lmArr.get(i).x, tmpMinLon);
            tmpMaxLat = Math.max(lmArr.get(i).y, tmpMaxLat);
            tmpMinLat = Math.min(lmArr.get(i).y, tmpMinLat);
        }
        ArrayList<Double> result = new ArrayList<Double>();
        result.add(tmpMaxLon);
        result.add(tmpMinLon);
        result.add(tmpMaxLat);
        result.add(tmpMinLat);
        // select the value which is furthest away from myLocation
        result.add(Math.max(
                Math.max(Math.abs(tmpMaxLon), Math.abs(tmpMinLon)),
                Math.max(Math.abs(tmpMaxLat), Math.abs(tmpMinLat))
        ));
        return result;
    }

    public void setLandmarksIntoLocalCoords(){
        for (int i=0; i<lmArr.size(); i++){
            lmArr.get(i).x = lmArr.get(i).x - myLocation.x;
            lmArr.get(i).y = lmArr.get(i).y - myLocation.y;
        }
        targetLocation.x -= myLocation.x;
        targetLocation.y -= myLocation.y;
        myLocation.x -= myLocation.x;
        myLocation.y -= myLocation.y;
    }

    // https://stackoverflow.com/questions/16266809/convert-from-latitude-longitude-to-x-y
    public void translateLatLonIntoXY(){
        int radiusEarth = 6371; // km
        for (int i=0; i<lmArr.size(); i++) {
            lmArr.get(i).x = Math.toRadians(lmArr.get(i).x) * radiusEarth * Math.cos(Math.toRadians(myLocation.y));
            lmArr.get(i).y = Math.toRadians(lmArr.get(i).y) * radiusEarth;
        }
        targetLocation.x = Math.toRadians(targetLocation.x) * radiusEarth * Math.cos(Math.toRadians(myLocation.y));
        targetLocation.y = Math.toRadians(targetLocation.y) * radiusEarth;
        myLocation.x = Math.toRadians(myLocation.x) * radiusEarth * Math.cos(Math.toRadians(myLocation.y));
        myLocation.y = Math.toRadians(myLocation.y) * radiusEarth;
    }

    // TODO: smartwatch resolution is hardcoded, should get resolution dynamically
    public void transformCoordsIntoCanvasResolution(){
        for (int i=0; i<lmArr.size(); i++) {
            lmArr.get(i).x = watchDisplayWidth/2 + lmArr.get(i).x * factor;
            lmArr.get(i).y = watchDisplayWidth/2 + lmArr.get(i).y * factor;
        }
        targetLocation.x = watchDisplayWidth/2 + targetLocation.x * factor;
        targetLocation.y = watchDisplayWidth/2 + targetLocation.y * factor;
        myLocation.x = watchDisplayWidth/2 + myLocation.x;
        myLocation.y = watchDisplayWidth/2 + myLocation.y;
    }

    public Landmark getMyLocation(){
        return myLocation;
    }

    public void calcFactor(double viewRadiusInKilometers){
        factor = (watchDisplayWidth/2 / (viewRadiusInKilometers));
    }
    public double getFactor(){
        return factor;
    }
    public ArrayList<Landmark> getLmArr() {
        return lmArr;
    }

    public Landmark getTargetLocation(){
        return targetLocation;
    }

    public void setMyLocation(Landmark myLoc){
        myLocation = myLoc;
    }

    public void setTargetLocation(Landmark targetLoc){
        targetLocation = targetLoc;
    }
}
