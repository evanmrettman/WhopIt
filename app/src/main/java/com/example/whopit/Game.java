package com.example.whopit;

import java.io.Serializable;
import java.util.Locale;

public class Game implements Comparable<Game>, Serializable
{
    int num = -1;
    int score = 0;

    public Game(int num,int score)
    {
        this.num = num;
        this.score = score;
    }

    @Override
    public int compareTo(Game game)
    {
        return Integer.compare(this.score,game.score);
    }

    @Override
    public String toString()
    {
        return String.format(Locale.getDefault(),"Game #%2d: %3d",this.num,this.score);
    }
}