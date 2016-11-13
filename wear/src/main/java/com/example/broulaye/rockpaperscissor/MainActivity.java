package com.example.broulaye.rockpaperscissor;

import android.graphics.Color;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.WearableListView;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends WearableActivity implements WearableListView.ClickListener{

    private BoxInsetLayout mContainerView;
    private Results results;
    private TextView score;
    WearableListView wearableListView;
    //The adapter object to add items to the list
    Adapter adapter;
    String[] elements = {"rock", "paper", "scissor"};
    CommHandler commHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();
        results = new Results();
        score = (TextView) findViewById(R.id.score);
        score.setText("Win: " + results.getWin() + "\t Loss: " + results.getLoss() + "\t Tie: " + results.getTie());
        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        wearableListView = (WearableListView) findViewById(R.id.wearList);

        /*
        * In the class we use wearableListView.addOnCentralPositionChangedListener(this);
        * This method will be fired whenever the user swaps the wearableListView.
        * If you only want a method be called when the user click the app,
        * use the setClickListener.
        * */

        wearableListView.setClickListener(this);
        //Initialize the Adapter
        adapter = new Adapter(this, elements);

        //Add the adapter to wearableListView
        wearableListView.setAdapter(adapter);

        commHandler=new CommHandler(this);
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
            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
        } else {
            mContainerView.setBackground(null);
        }
    }

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        int posi=viewHolder.getAdapterPosition();
        if(posi != -1) {
            System.out.println("Blocking user after interaction in wear");
            blockUser();
        }
        results.setMe(posi);


        if(results.getOpponent() != -1 && posi != -1) {
            System.out.println(results.getOpponent());
            updateScore();
            updateUI();
            System.out.println("Unblocking user after updating user and score in wear");
            unblockUser();
        }
        System.out.println("Sending message from wear with pos: " + posi);
        commHandler.sendMessage(posi);
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
        System.out.println("Reset values in Wear to -1");
        results.setMe(-1);
        results.setOpponent(-1);
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
}
