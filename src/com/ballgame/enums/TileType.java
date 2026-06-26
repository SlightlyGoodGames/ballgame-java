package com.ballgame.enums;

public enum TileType{
    BRICK("brick");
    private final String plainName;
    TileType(String plainName){
        this.plainName = plainName;
    }
    public String getPlainName(){
        return this.plainName;
    }
}