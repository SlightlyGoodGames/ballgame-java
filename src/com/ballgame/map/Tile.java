package com.ballgame.map;

import com.ballgame.enums.TileType;
import java.io.Serializable;

public class Tile implements Serializable{
    public TileType type;
    public int x;
    public int y;
    public Tile(TileType type,int x,int y){
        this.type = type;
        this.x = x;
        this.y = y;
    }
    @Override
    public boolean equals(Object o){
        if(!(o instanceof Tile)){return false;}
        if(o==this){return true;}
        Tile t = (Tile)o;
        return (t.type==this.type&&t.x==this.x&&t.y==this.y);
    }
    @Override
    public int hashCode(){
        return this.x*31+this.y+this.type.ordinal()*2000;
    }
}