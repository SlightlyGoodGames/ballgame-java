package com.ballgame.tiles;

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
}