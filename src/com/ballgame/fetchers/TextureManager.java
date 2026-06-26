package com.ballgame.fetchers;

import java.io.File;
import java.util.Map;
import java.util.HashMap;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class TextureManager{
    private static Map<String,BufferedImage> cachedTextures = new HashMap<String,BufferedImage>();
    public static BufferedImage findTexture(String textureCode){
        String fullPath = "assets/textures/"+textureCode.replace(".","/")+".png";
        try{
            BufferedImage texture = ImageIO.read(new File(fullPath));
            if(!cachedTextures.containsKey(textureCode)){
                cachedTextures.put(textureCode,texture);
            }
            return texture;
        } catch (Exception e){
            e.printStackTrace();
            return getTexture("global.unknown");
        }
    }
    public static BufferedImage getCachedTexture(String textureCode){
        return cachedTextures.get(textureCode);
    }
    public static BufferedImage getTexture(String textureCode){
        BufferedImage cacheAttempt = getCachedTexture(textureCode);
        if(cacheAttempt == null){
            return findTexture(textureCode);
        }
        return cacheAttempt;
    }
}