package com.example.broulaye.rockpaperscissor;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;

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
        Paper = (TextView) findViewById(R.id.paper);
        Scissor = (TextView) findViewById(R.id.scissor);
        score = (TextView) findViewById(R.id.score);
        results = new Results();
        score.setText("Win: " + results.getWin() + "\t Loss: " + results.getLoss() + "\t Tie: " + results.getTie());
        Rock.setOnClickListener(this);
        Paper.setOnClickListener(this);
        Scissor.setOnClickListener(this);
        commHandler = new CommHandler(this);

    }

    @Override
    public void onClick(View v) {
        String choice = ((TextView) v).getText().toString();
        switch (choice) {
            case "Rock":
                PlayIndex = 0;
                break;
            case "Paper":
                PlayIndex = 1;
                break;
            case "Scissor":
                PlayIndex = 2;
                break;
        }

        if(PlayIndex != -1) {
            System.out.println("Blocking user after interaction in mobile");
            blockUser();
        }

        results.setMe(PlayIndex);
        System.out.println("Sending message from phone");

        if(results.getOpponent() != -1 && results.getMe() != -1) {

            updateScore();
            updateUI();
            System.out.println("Unblocking user after updating user and score in mobile");
            unblockUser();
        }

        commHandler.sendMessage(PlayIndex);
        int notificationId = 1;
        Intent viewIntent = new Intent(this, MainActivity.class);
        PendingIntent viewPendingIntent = PendingIntent.getActivity(this, 0, viewIntent, 0);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this) .setSmallIcon(R.drawable.cast_ic_notification_small_icon) .setContentTitle("Color Picker") .setContentText("User selected color " + choice) .setContentIntent(viewPendingIntent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notificationId, notificationBuilder.build());
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
        switch (results.getOpponent()) {
            case 0:
                if(results.getMe() == 1) {//he has rock i got paper
                    results.incrementWin();
                }
                else if(results.getMe() == 0){//we both have rock
                    results.incrementTie();
                }
                else if(results.getMe() == 2) {//he has rock i have scissors
                    results.incrementLoss();
                }
                break;
            case 1:
                if(results.getMe() == 1) {//we both have paper
                    results.incrementTie();
                }
                else if(results.getMe() == 0){//he has paper i have rock
                    results.incrementLoss();
                }
                else if(results.getMe() == 2) {//he has paper i have scissors
                    results.incrementWin();
                }
                break;
            case 2:
                if(results.getMe() == 1) {//he has scissors i have paper
                    results.incrementLoss();
                }
                else if(results.getMe() == 0){//he has scissors i have rock
                    results.incrementWin();
                }
                else if(results.getMe() == 2) {//we both have scissors
                    results.incrementTie();
                }
                break;
            default:
                break;

        }

        System.out.println("Reset values in Mobile to -1");
        results.setMe(-1);
        results.setOpponent(-1);
    }

    public Results getResults(){
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
}
