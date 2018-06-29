package com.example.darlokh.test_smartwatch;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.metadude.java.library.overpass.ApiModule;
import info.metadude.java.library.overpass.OverpassService;
import info.metadude.java.library.overpass.models.Element;
import info.metadude.java.library.overpass.models.OverpassResponse;
import info.metadude.java.library.overpass.utils.NodesQuery;
import retrofit2.Call;
import retrofit2.Response;

import static junit.framework.Assert.fail;

public class asyncQuery_test extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    final String TAG = "TOASTBROT";
    public List<Element> elementsList;
    public View.OnClickListener handleClick = (new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Thread foo = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        OverpassService streamsService = ApiModule.provideOverpassService();
                        Map<String, String> tags = new HashMap<String, String>() {
                            {
                                put("amenity", "post_box");
                            }
                        };
                        NodesQuery nodesQuery = new NodesQuery(600, 52.516667, 13.383333, tags, true, 13);
                        Call<OverpassResponse> streamsResponseCall = streamsService.getOverpassResponse(
                                nodesQuery.getFormattedDataQuery());
                        Response<OverpassResponse> response = streamsResponseCall.execute();
                        if(response.isSuccessful()){
                            OverpassResponse overpassResponse = response.body();
                            elementsList = overpassResponse.elements;
                            Log.d(TAG, "run: response.isSuccessful");
                            for(int i = 0; i < elementsList.size(); i++){
                                Log.d(TAG, "elements: " + elementsList.get(i));
                            }
                        } else {
                            fail("Query failed.");
                        }} catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            foo.start();
        }
    });
}
