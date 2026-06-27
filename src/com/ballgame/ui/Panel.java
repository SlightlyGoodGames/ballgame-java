package com.ballgame.ui;

import java.awt.Graphics;
import javax.swing.JPanel;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Panel extends JPanel{
    private ArrayList<Component> img = new ArrayList<Component>();
    private ArrayList<Component> imgIterable;
    public int mouseX;
    public int mouseY;
    public ArrayList<Integer> keyPresses = new ArrayList<Integer>();
    public boolean mouseDown;
    private boolean isActive;
    public boolean releasedRecently;
    public Panel(){
        setup();
    }
    public Panel(ArrayList<Component> img){
        this.img = img;
        setup();
    }
    private void setup(){
        this.isActive = true;
        setFocusable(true);
        setPreferredSize(new Dimension(800,496));
        addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e) {
                Panel.this.mouseDown = true;
            }
            @Override
            public void mouseReleased(MouseEvent e){
                Panel.this.mouseDown = false;
                Panel.this.releasedRecently = true;
            }
        });
        addMouseMotionListener(new MouseAdapter(){
            @Override
            public void mouseMoved(MouseEvent e){
                Panel.this.mouseX = e.getX();
                Panel.this.mouseY = e.getY();
            }
            @Override
            public void mouseDragged(MouseEvent e){
                Panel.this.mouseX = e.getX();
                Panel.this.mouseY = e.getY();
            }
        });
        addKeyListener(new KeyListener(){
            @Override
            public void keyPressed(KeyEvent e){
                Panel.this.keyPresses.add(e.getKeyCode());
            }
            @Override
            public void keyTyped(KeyEvent e){}
            @Override
            public void keyReleased(KeyEvent e){}
        });
    }
    @Override
    @SuppressWarnings("unchecked")
    public void paintComponent(Graphics g){
        if(isActive){
            super.paintComponent(g);
            imgIterable = (ArrayList<Component>)this.img.clone();
            for(Component comp : imgIterable){
                g.drawImage(comp.img,comp.x,comp.y,null);
            }
        }
    }
    public void addImg(Component imageToAdd){
        this.img.add(imageToAdd);
    }
    public void addImg(BufferedImage imageToAdd,int x,int y){
        this.img.add(new Component(imageToAdd,x,y));
    }
    public void clearCanvas(){
        this.img = new ArrayList<Component>();
    }
    public void copy(Panel p){
        this.img = p.img;
    }
    public void tick(){
        this.releasedRecently = false;
    }
    public ArrayList<Integer> readKeyPresses(){
        @SuppressWarnings("unchecked")
        ArrayList<Integer> presses = (ArrayList<Integer>)this.keyPresses.clone();
        this.keyPresses.clear();
        return presses;
    }
}