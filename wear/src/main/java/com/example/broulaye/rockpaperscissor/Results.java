package com.example.broulaye.rockpaperscissor;

/**
 * Created by Broulaye on 11/11/2016.
 */

public class Results {
    int win, loss,tie, me, opponent;

    public Results() {
        this.win = 0;
        this.loss = 0;
        this.tie = 0;
        this.me = -1;
        this.opponent = -1;
    }

    public int getOpponent() {
        return opponent;
    }

    public void setOpponent(int opponent) {
        this.opponent = opponent;
    }

    public int getMe() {
        return me;
    }

    public void setMe(int me) {
        this.me = me;
    }

    public int getLoss() {
        return loss;
    }

    public int getWin() {
        return win;
    }

    public int getTie() {
        return tie;
    }

    public void incrementWin(){
        win++;
    }

    public void incrementLoss(){
        loss++;
    }

    public void incrementTie(){
        tie++;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public void setLoss(int loss) {
        this.loss = loss;
    }

    public void setTie(int tie) {
        this.tie = tie;
    }
}
