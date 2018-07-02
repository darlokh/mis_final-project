package com.example.darlokh.test_smartwatch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class DataLayerListenerService extends WearableListenerService {

    public JSONArray newJSONArray;

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);

        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        for(DataEvent event : events) {
            final Uri uri = event.getDataItem().getUri();
            final String path = uri!=null ? uri.getPath() : null;
            if("/landmarkData".equals(path)) {
                final DataMap map = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                // read your values from map:
                String stringExample = map.getString("com.example.key.landmarkdata");
//                System.out.println(stringExample);

                Intent dataIntent = new Intent();
                dataIntent.setAction(Intent.ACTION_SEND);
                dataIntent.putExtra("data", stringExample);
                LocalBroadcastManager.getInstance(this).sendBroadcast(dataIntent);

//                try {
//                    JSONObject newJSON = new JSONObject(stringExample);
//                    JSONArray newJSONArray = newJSON.getJSONArray("locations");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                System.out.println(newJSONArray);
//


            }
        }
    }
}