package com.dobbinsoft.netting.base.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtils {

    public static final Properties properties = new Properties();

    static {
        try (InputStream is = PropertyUtils.class.getResourceAsStream("/netting-im.properties")){
            if (is != null) {
                properties.load(is);
            }
        } catch (IOException e) {
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static Integer getPropertyInt(String key) {
        return Integer.parseInt(getProperty(key));
    }



}
