package com.ballgame.map;

import java.io.Serializable;

public class Coord implements Serializable{
    int x;
    int y;
    public Coord(int x,int y){
        this.x = x;
        this.y = y;
    }
    @Override
    public boolean equals(Object o){
        if(this==o){return true;}
        if(!(o instanceof Coord)){return false;}
        Coord c = (Coord) o;
        return (c.x==this.x&&c.y==this.y);
    }
    @Override
    public int hashCode(){
        return this.x*31+this.y;
    }
}