package com.ballgame.ui;

import java.awt.image.BufferedImage;

public class Component{
    public BufferedImage img;
    public int x;
    public int y;
    public Component(BufferedImage img,int x,int y){
        this.img = img;
        this.x = x;
        this.y = y;
    }
}