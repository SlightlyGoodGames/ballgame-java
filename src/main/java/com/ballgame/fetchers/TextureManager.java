package com.ballgame.fetchers;

import java.util.Map;
import java.util.HashMap;

import javax.imageio.ImageIO;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.image.BufferedImage;

public class TextureManager{
    private static Map<String,BufferedImage> cachedTextures = new HashMap<String,BufferedImage>();
    private static Map<String,Map<String,Object>> cachedMetas = new HashMap<String,Map<String,Object>>();
    public static BufferedImage findTexture(String textureCode){
        String fullPath = getFullPath(textureCode)+"/default.png";
        try{
            BufferedImage texture = ImageIO.read(TextureManager.class.getResourceAsStream(fullPath));
            if(!cachedTextures.containsKey(textureCode)){
                cachedTextures.put(textureCode,texture);
            }
            return texture;
        } catch (Exception e){
            if(textureCode.equals("global.unknown")){
                return null;
            }
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
    public static BufferedImage getRawTexture(String textureCode){
        BufferedImage cacheAttempt = getCachedTexture(textureCode);
        if(cacheAttempt == null){
            return findRawTexture(textureCode);
        }
        return cacheAttempt;
    }
    public static BufferedImage findRawTexture(String textureCode){
        String fullPath = getFullPath(textureCode)+".png";
        try{
            BufferedImage texture = ImageIO.read(TextureManager.class.getResourceAsStream(fullPath));
            if(!cachedTextures.containsKey(textureCode)){
                cachedTextures.put(textureCode,texture);
            }
            return texture;
        } catch (Exception e){
            if(textureCode.equals("global.unknown")){
                return null;
            }
            return getTexture("global.unknown");
        }
    }
    public static String getFullPath(String textureCode){
        return "/textures/"+textureCode.replace(".","/");
    }
    public static Map<String,Object> findTextureMeta(String textureCode){
        String file = getFullPath(textureCode)+"/meta.json";
        ObjectMapper objMapper = new ObjectMapper();
        try{
            @SuppressWarnings("unchecked")
            Map<String,Object> map = objMapper.readValue(TextureManager.class.getResourceAsStream(file),Map.class);
            if(!cachedMetas.containsKey(textureCode)){
                cachedMetas.put(textureCode,map);
            }
            return map;
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Failed to load");
        }
        return null;
    }
    public static Map<String,Object> getTextureMeta(String textureCode){
        Map<String,Object> cacheAttempt = getCachedMeta(textureCode);
        if(cacheAttempt == null){
            return findTextureMeta(textureCode);
        }
        return cacheAttempt;
    }
    public static Map<String,Object> getCachedMeta(String textureCode){
        return cachedMetas.get(textureCode);
    }
}