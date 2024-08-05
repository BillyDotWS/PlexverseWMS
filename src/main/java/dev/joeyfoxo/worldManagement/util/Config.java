package dev.joeyfoxo.worldManagement.util;

import dev.joeyfoxo.worldManagement.WorldManagement;

public class Config {

    public static String readFromConfig(String key) {
        return WorldManagement.getInstance().getConfig().getString(key);
    }


}
