package com.example.broulaye.rockpaperscissor;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
/**
 * Created by Shuo on 11/3/2016.
 */

public class CommHandler implements
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    MainActivity mainActivity;
    GoogleApiClient googleApiClient;
    Results results;
    static Handler UIHandler=null;

    public static final String CURRENT_MCHOICE_INDEX = "current choice mobile";
    public static final String CURRENT_WCHOICE_INDEX = "current choice wear";

    public CommHandler(final MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        googleApiClient = new GoogleApiClient.Builder(mainActivity)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
        results = mainActivity.getResults();

        /**
        UIHandler=new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {

                System.out.println("Mobile Me: " + results.getMe());
                System.out.println("Mobile Opponent: " + results.getOpponent());
                //results.setOpponent((int)msg.obj);
                if(results.getOpponent() != -1) {

                    mainActivity.updateScore();
                    mainActivity.updateUI();
                }
                super.handleMessage(msg);
            }
        };*/
    }

    public void sendMessage(int currentIndex){
        new NumberSenderAsync().execute(currentIndex);
    }
    private class NumberSenderAsync extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/" + CURRENT_MCHOICE_INDEX);
            putDataMapRequest.getDataMap().putInt(CURRENT_MCHOICE_INDEX, params[0]);
            putDataMapRequest.getDataMap().putLong("time", System.currentTimeMillis());
            PutDataRequest putDataRequest=putDataMapRequest.asPutDataRequest();
            Wearable.DataApi.putDataItem(googleApiClient,putDataRequest);
            return null;
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Wearable.DataApi.addListener(googleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        for(DataEvent event:dataEventBuffer){
            System.out.println("OnDataChanged: ");
            if(event.getType()== DataEvent.TYPE_CHANGED){
                DataItem item=event.getDataItem();
                if(item.getUri().getPath().compareTo("/"+ CURRENT_WCHOICE_INDEX)==0){
                    DataMap dataMap= DataMapItem.fromDataItem(item).getDataMap();
                    int choiceIndex=dataMap.getInt(CURRENT_WCHOICE_INDEX);
                    //System.out.println("received change from watch with choice: " + choiceIndex);
                    //results.setOpponent(choiceIndex);
                    results.setOpponent(choiceIndex);

                    System.out.println("Open hand:" + results.getOpponent() + " My hand:" + results.getMe());
                    if(results.getMe() != -1 && results.getOpponent() != -1) {
                        mainActivity.updateScore();
                        mainActivity.updateUI();

                        System.out.println("Unblocking user after updating user and score in mobile");
                        mainActivity.unblockUser();
                    }

                    //android.os.Message msg=UIHandler.obtainMessage(0,choiceIndex);
                    //msg.sendToTarget();
                }
            }
        }
    }
}
