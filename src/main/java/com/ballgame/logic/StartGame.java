package com.ballgame.logic;

import static com.ballgame.fetchers.LangManager.*;
import static com.ballgame.fetchers.TextureManager.*;
import com.ballgame.player.Player;
import com.ballgame.ui.Component;
import com.ballgame.ui.Panel;
import com.ballgame.ui.Window;
import com.ballgame.enums.TileType;
import com.ballgame.map.Coord;
import com.ballgame.map.Tile;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.FileInputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

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
        } catch (IOException | ClassNotFoundException e){
            System.out.println("Deserialisation failed!");
        }
        return null;
    }
}

enum MenuMode{
    EDITOR,ERROR,QUIT,DEBUG;
}

enum EditorPlaceMode{
    INVALID,PLACE,DELETE;
}

class Menu{
    public static MenuMode autoLaunchMenu(MenuMode mode){
        switch(mode){
            case EDITOR:
                return launchEditor();
            case ERROR:
                return launchEditor();
            case QUIT:
                UI.frame.dispatchEvent(new WindowEvent(UI.frame, WindowEvent.WINDOW_CLOSING));
                System.exit(0);
            case DEBUG:
                return launchDebugCode();
        }
        return MenuMode.ERROR;
    }
    public static MenuMode launchDebugCode(){
        return MenuMode.EDITOR;
    }
    @SuppressWarnings("unchecked")
    public static MenuMode launchEditor(){
        Player player = new Player(800,0);
        Map<Coord,Tile> allTiles = new HashMap<Coord,Tile>();
        try{
            allTiles = (Map<Coord,Tile>) Saver.load("generated/saves/map.dat");
        } catch (Exception e){
            System.out.println("Error reading from file!");
        }
        if(allTiles == null){
            allTiles = new HashMap<Coord,Tile>();
        }
        BufferedImage img;
        boolean[] groupsAround;
        boolean hasExited;
        int index;
        long currentTimeMillis = System.currentTimeMillis();
        long animResetMillis = System.currentTimeMillis();
        int frameAmount = 0;
        EditorPlaceMode placeMode = EditorPlaceMode.INVALID;
        ArrayList<Integer> keyPresses;
        TileType placeType = TileType.BRICK;
        String textureCode;
        Map<String,Object> textureMeta;
        Map<String,Object> globalTextureMeta;
        Map<String,String> images;
        ArrayList<Map<String,Object>> allFrames;
        boolean replaceUsed;
        long animationFrames = 0;
        int nextAnim;
        double totalAnimMs;
        while(true){
            UI.clear();
            for(Tile renderTile : allTiles.values()){
                textureCode = "map.tile."+renderTile.type.getPlainName();
                globalTextureMeta = getTextureMeta(textureCode);
                nextAnim = 0;
                allFrames = (ArrayList<Map<String,Object>>)globalTextureMeta.get("frames");
                totalAnimMs = (double)globalTextureMeta.get("loopfr");
                for(Map<String,Object> s : allFrames){
                    if((double)s.get("begin") <= animationFrames%totalAnimMs){
                        nextAnim++;
                    } else {
                        break;
                    }
                }
                textureMeta = ((ArrayList<Map<String,Object>>)globalTextureMeta.get("frames")).get(nextAnim-1);
                images = (Map<String,String>)textureMeta.get("images");
                if(textureMeta.get("method").equals("add") || textureMeta.get("method").equals("none")){
                    img = getRawTexture(images.get("default"));
                    UI.addObj(img,renderTile.x,renderTile.y);
                }
                if(!(textureMeta.get("method").equals("none"))){
                    groupsAround = new boolean[]{false,false,false,false,false,false,false,false,false};

                    for(index=0;index<=8;index++){
                        groupsAround[index] = allTiles.containsValue(new Tile(renderTile.type,renderTile.x+(index % 3)*32-32,renderTile.y+(index/3)*32-32));
                    }

                    replaceUsed = false;
                    index = 0;
                    for(boolean b : groupsAround){
                        if(((!b && textureMeta.get("usewhen").equals("not")) || (b && textureMeta.get("usewhen").equals("is"))) && images.containsKey(String.valueOf(index))){
                            img = getRawTexture(images.get(String.valueOf(index)));
                            UI.addObj(img,renderTile.x,renderTile.y);
                            replaceUsed = true;
                        }
                        index++;
                    }
                    if(!replaceUsed && textureMeta.get("method").equals("replace")){
                        img = getRawTexture((String)images.get("default"));
                        UI.addObj(img,renderTile.x,renderTile.y);
                    }
                }
            }

            //Mouse handling
            if(UI.canvas.mouseDown){
                int[] mouseTilePos = new int[]{((int)UI.canvas.mouseX/32)*32,((int)UI.canvas.mouseY/32)*32};
                hasExited = false;
                if(placeMode == EditorPlaceMode.INVALID || placeMode == EditorPlaceMode.DELETE){
                    for(Tile t : allTiles.values()){
                        if(mouseTilePos[0] == t.x && mouseTilePos[1] == t.y){
                            allTiles.remove(new Coord(mouseTilePos[0],mouseTilePos[1]));
                            hasExited = true;
                            placeMode = EditorPlaceMode.DELETE;
                            break;
                        }
                    }
                }
                if(!hasExited && (placeMode == EditorPlaceMode.INVALID || placeMode == EditorPlaceMode.PLACE) && !allTiles.containsValue(new Tile(placeType,mouseTilePos[0],mouseTilePos[1]))){
                    allTiles.put(new Coord(mouseTilePos[0],mouseTilePos[1]),new Tile(placeType,mouseTilePos[0],mouseTilePos[1]));
                    placeMode = EditorPlaceMode.PLACE;
                }
                if(mouseTilePos[0] > 800){
                    Saver.save((Serializable)allTiles,"generated/saves/map.dat");
                }
            } else {
                placeMode = EditorPlaceMode.INVALID;
            }

            //Key handling
            keyPresses = UI.canvas.readKeyPresses();
            for(int k : keyPresses){
                if(k == KeyEvent.VK_1){
                    placeType = TileType.BRICK;
                } else if(k == KeyEvent.VK_2){
                    placeType = TileType.SPIKE;
                } else if(k == KeyEvent.VK_3){
                    placeType = TileType.GLUE;
                } else if(k == KeyEvent.VK_4){
                    placeType = TileType.LADDER;
                } else if(k == KeyEvent.VK_5){
                    placeType = TileType.FLAG;
                } else if(k == KeyEvent.VK_Q){
                    return MenuMode.QUIT;
                } else if(k == KeyEvent.VK_C){
                    allTiles.clear();
                }
            }

            img = getTexture("map.player.skin1");
            UI.addObj(img,player.x,player.y);
            UI.update();
            frameAmount++;
            if(System.currentTimeMillis()-currentTimeMillis >= 1000){
                System.out.print("\r\033[KCurrent FPS: "+frameAmount);
                currentTimeMillis = System.currentTimeMillis();
                frameAmount = 0;
            }
            if(System.currentTimeMillis()-animResetMillis >= 30000){
                animResetMillis = System.currentTimeMillis();
            }
            animationFrames = (System.currentTimeMillis()-animResetMillis)/20L;
        }
    }
}

public class StartGame{
    public static void main(String[] args){
        UI.setup();
        MenuMode newMode = MenuMode.DEBUG;
        while(true){
            newMode = Menu.autoLaunchMenu(newMode);
        }
    }
}