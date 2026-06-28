package com.ballgame.fetchers;

import java.util.Properties;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import java.io.InputStream;

public class LangManager{
    private static Map<String,String> cachedStrings = new HashMap<String,String>();
    public static String findString(String stringCode){
        String[] pathComponents = stringCode.split("\\.");
        String finalCode = pathComponents[pathComponents.length-1];
        String[] splitPath = Arrays.copyOfRange(pathComponents,0,pathComponents.length-1);
        String fullPath = "/lang/"+String.join("/",splitPath)+".properties";
        try{
            InputStream fileIn = LangManager.class.getResourceAsStream(fullPath);
            Properties fileParameters = new Properties();
            fileParameters.load(fileIn);
            String string = fileParameters.getProperty(finalCode);
            if(!cachedStrings.containsKey(stringCode)){
                cachedStrings.put(stringCode,string);
            }
            return string;
        } catch(Exception e) {
            return stringCode;
        }
    }
    public static String getCachedString(String stringCode){
        return cachedStrings.get(stringCode);
    }
    public static String getString(String stringCode){
        String cacheAttempt = getCachedString(stringCode);
        if(cacheAttempt == null){
            return findString(stringCode);
        }
        return cacheAttempt;
    }
}