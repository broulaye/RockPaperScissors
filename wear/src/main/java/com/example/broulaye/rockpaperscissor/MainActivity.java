package com.example.broulaye.rockpaperscissor;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends WearableActivity implements WearableListView.ClickListener{

    private BoxInsetLayout mContainerView;
    private Results results;
    private TextView score;
    WearableListView.ViewHolder viewHolder2;
    WearableListView wearableListView;
    WearableListView wearableListView2;
    //The adapter object to add items to the list
    Adapter adapter;
    String[] elements = {"✊", "✋", "✌"};
    CommHandler commHandler;
    private int playIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();
        playIndex = -1;
        results = new Results();
        LoadPreferences();
        score = (TextView) findViewById(R.id.score);
        score.setText("Win: " + results.getWin() + "\t Loss: " + results.getLoss() + "\t Tie: " + results.getTie());
        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        wearableListView = (WearableListView) findViewById(R.id.wearList);
        wearableListView2 = (WearableListView) findViewById(R.id.wearList2);

        /*
        * In the class we use wearableListView.addOnCentralPositionChangedListener(this);
        * This method will be fired whenever the user swaps the wearableListView.
        * If you only want a method be called when the user click the app,
        * use the setClickListener.
        * */


        //Initialize the Adapter
        adapter = new Adapter(this, elements);

        //Add the adapter to wearableListView
        wearableListView.setAdapter(adapter);
        //wearableListView2.setAdapter(adapter);
        //wearableListView2.getChildAt(1).setBackgroundColor(Color.BLUE);
        wearableListView.setClickListener(this);
        commHandler=new CommHandler(this);



    }

    public void sendMessage(final String path, final String text, final GoogleApiClient mApiClient ) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
                for(Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiClient, node.getId(), path, text.getBytes() ).await();
                }

                runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        //mEditText.setText( "" );
                    }
                });
            }
        }).start();
    }

    public void sendNotification() {
        int notificationId = 001;
        // Build intent for notification content
        Intent viewIntent = new Intent(this, MainActivity.class);
        //viewIntent.putExtra(EXTRA_EVENT_ID, eventId);
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(this, 0, viewIntent, 0);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.open_on_phone)
                        .setContentTitle("RockPaperScissors!")
                        .setContentText("Let's Play!")
                        .setContentIntent(viewPendingIntent);

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.white));
        } else {
            mContainerView.setBackground(null);
        }
    }

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        playIndex = viewHolder.getAdapterPosition();
        viewHolder2 = viewHolder;
        viewHolder.itemView.setBackgroundColor(Color.BLUE);
        //playIndex = posi;
        if(playIndex != -1) {
            System.out.println("Blocking user after interaction in wear");
            blockUser();
        }
        results.setMe(playIndex);



        if(results.getOpponent() != -1 && results.getMe() != -1) {
            System.out.println(results.getOpponent());
            updateScore();
            updateUI();
            System.out.println("Unblocking user after updating user and score in wear");
            unblockUser();
            viewHolder.itemView.setBackgroundColor(Color.WHITE);
        }
        System.out.println("Sending message from wear with pos: " + playIndex);
        commHandler.sendMessage(playIndex);
    }

    public void updateScore() {
        String message = "";
        switch (results.getOpponent()) {
            case 0:
                if (results.getMe() == 1) {//he has rock i got paper
                    results.incrementWin();
                    message = "He played rock you played paper.\nYOU WIN";
                } else if (results.getMe() == 0) {//we both have rock
                    results.incrementTie();
                    message = "You both played rock.\nIT'S A TIE";
                } else if (results.getMe() == 2) {//he has rock i have scissors
                    results.incrementLoss();
                    message = "He played rock you played scissors.\nYOU LOOSE";
                }
                break;
            case 1:
                if (results.getMe() == 1) {//we both have paper
                    results.incrementTie();
                    message = "You both played paper.\nIT'S A TIE";
                } else if (results.getMe() == 0) {//he has paper i have rock
                    results.incrementLoss();
                    message = "He played paper you played rock.\nYOU LOOSE";
                } else if (results.getMe() == 2) {//he has paper i have scissors
                    results.incrementWin();
                    message = "He played paper you played scissors.\nYOU WIN";
                }
                break;
            case 2:
                if (results.getMe() == 1) {//he has scissors i have paper
                    results.incrementLoss();
                    message = "He played scissors you played paper.\nYOU LOOSE";
                } else if (results.getMe() == 0) {//he has scissors i have rock
                    results.incrementWin();
                    message = "He played scissors you played rock.\nYOU WIN";
                } else if (results.getMe() == 2) {//we both have scissors
                    results.incrementTie();
                    message = "You both played scissors.\nIT'S A TIE";
                }
                break;
            default:
                break;

        }

        System.out.println("Reset values in Mobile to -1");
        results.setMe(-1);
        results.setOpponent(-1);
        showDialog(message);
    }

    public Results getResults(){
        return results;
    }

    public void updateUI() {
        score.setText("Win: " + results.getWin() + " Loss: " + results.getLoss() + " Tie: " + results.getTie());
    }

    @Override
    public void onTopEmptyRegionClick() {

    }

    public void blockUser() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void unblockUser() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SavePreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadPreferences();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SavePreferences();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("Me", results.getMe());
        outState.putInt("Opponent", results.getOpponent());
        outState.putInt("Win", results.getWin());
        outState.putInt("Loss", results.getLoss());
        outState.putInt("Tie", results.getTie());
        SavePreferences();
        super.onSaveInstanceState(outState);
    }

    private void SavePreferences() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        Log.e("MainActivity", "SavePreferences");

        editor.putInt("Me", results.getMe());
        editor.putInt("Opponent", results.getOpponent());
        editor.putInt("Win", results.getWin());
        editor.putInt("Loss", results.getLoss());
        editor.putInt("Tie", results.getTie());


        editor.apply();
    }


    private void LoadPreferences() {
        Log.e("MainActivity", "LoadPreferences");
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        results.setMe(sharedPreferences.getInt("Me", -1));
        results.setOpponent(sharedPreferences.getInt("Opponent", -1));
        //results.setMe(-1);
        //results.setOpponent(-1);
        results.setLoss(sharedPreferences.getInt("Loss", 0));
        results.setWin(sharedPreferences.getInt("Win", 0));
        results.setTie(sharedPreferences.getInt("Tie", 0));
        if(results.getMe() != -1) {
            blockUser();
            if(results.getMe() == 2) {
                setViewColor(results.getMe());
            }
            else {

            }
        }
        else {
            unblockUser();
        }
    }

    public void showDialog(String msg) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(msg);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


    public void resetViewColor(WearableListView.ViewHolder holder) {
        holder.itemView.setBackgroundColor(Color.WHITE);
    }

    public void setViewColor(int index) {
        wearableListView.getChildAt(index).setBackgroundColor(Color.BLUE);
    }

    public WearableListView.ViewHolder getViewHolder(){
        return viewHolder2;
    }

}
