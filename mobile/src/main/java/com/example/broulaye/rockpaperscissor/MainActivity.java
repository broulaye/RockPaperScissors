package com.example.broulaye.rockpaperscissor;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ScrollView bg;
    private int PlayIndex = -1;
    private TextView Rock, Paper, Scissor, score;
    private CommHandler commHandler;
    private Results results;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bg = (ScrollView) findViewById(R.id.bg);
        Rock = (TextView) findViewById(R.id.rock);
        Rock.setBackgroundColor(Color.WHITE);
        Paper = (TextView) findViewById(R.id.paper);
        Paper.setBackgroundColor(Color.WHITE);
        Scissor = (TextView) findViewById(R.id.scissor);
        Scissor.setBackgroundColor(Color.WHITE);
        score = (TextView) findViewById(R.id.score);
        score.setBackgroundColor(Color.WHITE);
        results = new Results();
        LoadPreferences();
        score.setText("Win: " + results.getWin() + "\t Loss: " + results.getLoss() + "\t Tie: " + results.getTie());
        Rock.setOnClickListener(this);
        Paper.setOnClickListener(this);
        Scissor.setOnClickListener(this);
        commHandler = new CommHandler(this);
        sendNotification();


    }



    public void sendNotification() {
        int notificationId = 001;
        // Build intent for notification content
        Intent viewIntent = new Intent(this, MainActivity.class);
        //viewIntent.putExtra(EXTRA_EVENT_ID, eventId);
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(this, 0, viewIntent, 0);

        android.support.v4.app.NotificationCompat.Builder notificationBuilder =
                new android.support.v4.app.NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
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
    protected void onDestroy() {
        super.onDestroy();
        commHandler.disconect();
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

    @Override
    public void onClick(View v) {
        String choice = ((TextView) v).getText().toString();
        v.setBackgroundColor(Color.BLUE);
        switch (choice) {
            case "✊":
                PlayIndex = 0;
                break;
            case "✋":
                PlayIndex = 1;
                break;
            case "✌":
                PlayIndex = 2;
                break;
        }

        if (PlayIndex != -1) {
            System.out.println("Blocking user after interaction in mobile");

            blockUser();
        }
        results.setMe(PlayIndex);


        System.out.println("Sending message from phone");

        if (results.getOpponent() != -1 && results.getMe() != -1) {

            updateScore();
            updateUI();
            System.out.println("Unblocking user after updating user and score in mobile");
            unblockUser();
            v.setBackgroundColor(Color.WHITE);
        }

        commHandler.sendMessage(PlayIndex);

//        //Send a notification to the watch
//        int notificationID = 1;
//        //The intent allows user opens the activity on the phone
//        Intent viewIntent = new Intent(this, MainActivity.class);
//        PendingIntent viewPendingIntent = PendingIntent.getActivity(this, 0, viewIntent, 0);
//        //Use the notification builder to create a notification
//        NotificationCompat.Builder notificationBuilder =
//                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
//                        .setSmallIcon(R.drawable.cast_ic_notification_small_icon)
//                        .setContentTitle("Simple Color Picker")
//                        .setContentText("User has picked color : " + color)
//                        .setContentIntent(viewPendingIntent);
//        //Send the notification
//        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
//        notificationManagerCompat.notify(notificationID, notificationBuilder.build());
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

    public Results getResults() {
        return results;
    }

    public void updateUI() {
        score.setText("Win: " + results.getWin() + " Loss: " + results.getLoss() + " Tie: " + results.getTie());
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
        LoadPreferences();
        super.onResume();
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
        if (results.getMe() != -1) {
            blockUser();
            setViewColor(results.getMe());
        } else {
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

    public void resetViewColor(int index) {
        switch (index) {
            case 0://rock
                Rock.setBackgroundColor(Color.WHITE);
                break;
            case 1:
                Paper.setBackgroundColor(Color.WHITE);
                break;
            case 2:
                Scissor.setBackgroundColor(Color.WHITE);
                break;
            default:
                break;
        }
    }

    public void setViewColor(int index) {
        switch (index) {
            case 0://rock
                Rock.setBackgroundColor(Color.BLUE);
                break;
            case 1:
                Paper.setBackgroundColor(Color.BLUE);
                break;
            case 2:
                Scissor.setBackgroundColor(Color.BLUE);
                break;
            default:
                break;
        }
    }

}
