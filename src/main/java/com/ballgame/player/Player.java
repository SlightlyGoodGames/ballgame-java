package com.ballgame.player;

public class Player{
    public int x;
    public int y;
    public int gravity;
    public Player(int startX,int startY){
        this.x = startX;
        this.y = startY;
        this.gravity = 0;
    }
}