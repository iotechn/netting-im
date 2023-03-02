package com.dobbinsoft.netting.base.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class PropertyUtils {

    public static final Properties properties = new Properties();

    public static void init(String active){
        if (StringUtils.isNotEmpty(active)) {
            log.info("The active profile: {}", active);
        }
        String name = StringUtils.isNotEmpty(active) ? ("/netting-im-" + active +".properties") : "/netting-im.properties";
        try (InputStream is = PropertyUtils.class.getResourceAsStream(name)){
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
