package com.ballgame.ui;

import javax.swing.JFrame;

public class Window extends JFrame{
    public Window(String title,int width,int height){
        setTitle(title);
        setSize(width,height);
        setResizable(false);
        setDefaultCloseOperation(3);
        setLocationRelativeTo(null);
    }
}