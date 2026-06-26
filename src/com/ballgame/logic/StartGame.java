package com.ballgame.logic;

import static com.ballgame.fetchers.LangManager.*;
import static com.ballgame.fetchers.TextureManager.*;
import com.ballgame.player.Player;
import com.ballgame.ui.*;
import com.ballgame.tiles.Tile;
import com.ballgame.enums.*;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.io.Serializable;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.FileInputStream;

class UI{
    static Window frame = new Window(getString("external.window.title"),800,496);
    static Panel canvas = new Panel();
    static Panel newCanvas = new Panel();
    static void setup(){
        frame.getContentPane().add(canvas);
        frame.setVisible(true);
    }
    static void addObj(Component c){
        newCanvas.addImg(c);
    }
    static void addObj(BufferedImage img,int x,int y){
        newCanvas.addImg(img,x,y);
    }
    static void update(){
        canvas.copy(newCanvas);
        frame.repaint();
    }
    static void clear(){
        newCanvas.clearCanvas();
    }
}

class Saver{
    static void save(Serializable s,String file){
        try{
            Path filePath = Path.of(file);
            Files.createDirectories(filePath.getParent());
            if(Files.notExists(filePath)){
                Files.createFile(filePath);
            }
            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
            objOut.writeObject(s);
            objOut.close();
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("Serialisation failed");
        }
    }
    static Serializable load(String file){
        try{
            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream objIn = new ObjectInputStream(fileIn);
            Serializable obj = (Serializable)objIn.readObject();
            objIn.close();
            return obj;
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Deserialisation failed!");
        }
        return null;
    }
}

enum MenuMode{
    EDITOR,ERROR;
}

enum EditorPlaceMode{
    INVALID,PLACE,DELETE;
}

class Menu{
    public static MenuMode autoLaunchMenu(MenuMode mode){
        switch(mode){
            case MenuMode.EDITOR:
                return launchEditor();
            case MenuMode.ERROR:
                return launchEditor();
        }
        return MenuMode.ERROR;
    }
    @SuppressWarnings("unchecked")
    public static MenuMode launchEditor(){
        Player player = new Player(800,0);
        ArrayList<Tile> allTiles = new ArrayList<Tile>();
        try{
            allTiles = (ArrayList<Tile>) Saver.load("generated/saves/map.dat");
        } catch (Exception e){
            System.out.println("Error reading from file!");
        }
        BufferedImage img;
        boolean[] groupsAround;
        int dx;
        int dy;
        boolean hasExited;
        int index;
        long currentTimeMillis = System.nanoTime();
        EditorPlaceMode placeMode = EditorPlaceMode.INVALID;
        while(true){
            UI.clear();
            for(Tile renderTile : allTiles){
                groupsAround = new boolean[]{false,false,false,false,false,false,false,false,false};
                img = getTexture("map.tile."+renderTile.type.getPlainName());
                UI.addObj(img,renderTile.x,renderTile.y);

                for(Tile t : allTiles){
                    dx = (t.x-renderTile.x)/32;
                    dy = (t.y-renderTile.y)/32;
                    if (-1 <= dx && dx <= 1 && -1 <= dy && dy <= 1) {
                        groupsAround[(dy+1)*3+(dx+1)] = true;
                    }
                }
                
                index = 0;
                for(boolean b : groupsAround){
                    if(!b){
                        img = getTexture("map.tile.outlines.air"+index);
                        UI.addObj(img,renderTile.x,renderTile.y);
                    }
                    index++;
                }
            }

            if(UI.canvas.mouseDown){
                int[] mouseTilePos = new int[]{((int)UI.canvas.mouseX/32)*32,((int)UI.canvas.mouseY/32)*32};
                hasExited = false;
                if(placeMode == EditorPlaceMode.INVALID || placeMode == EditorPlaceMode.DELETE){
                    for(Tile t : allTiles){
                        if(mouseTilePos[0] == t.x && mouseTilePos[1] == t.y){
                            allTiles.remove(t);
                            hasExited = true;
                            placeMode = EditorPlaceMode.DELETE;
                            break;
                        }
                    }
                }
                if(!hasExited && (placeMode == EditorPlaceMode.INVALID || placeMode == EditorPlaceMode.PLACE)){
                    allTiles.add(new Tile(TileType.BRICK,mouseTilePos[0],mouseTilePos[1]));
                    placeMode = EditorPlaceMode.PLACE;
                }
                if(mouseTilePos[0] > 800){
                    Saver.save(allTiles,"generated/saves/map.dat");
                }
            } else {
                placeMode = EditorPlaceMode.INVALID;
            }
            img = getTexture("map.player.skin1");
            UI.addObj(img,player.x,player.y);
            UI.update();
            try{
                Thread.sleep(1);
                System.out.println("Current FPS: "+1000000000/(System.nanoTime()-currentTimeMillis));
                currentTimeMillis = System.nanoTime();
            } catch(Exception e){
                e.printStackTrace();
                System.out.println("Possible InterruptedException!");
            }
        }
    }
}

public class StartGame{
    public static void main(String[] args){
        UI.setup();
        MenuMode newMode = MenuMode.EDITOR;
        while(true){
            newMode = Menu.autoLaunchMenu(newMode);
        }
    }
}