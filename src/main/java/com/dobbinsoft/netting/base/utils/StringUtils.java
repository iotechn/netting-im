package com.dobbinsoft.netting.base.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StringUtils {

    public static boolean isEmpty(String string) {
        return string == null || string.trim().isEmpty();
    }

    public static boolean isNotEmpty(String string) {
        return !isEmpty(string);
    }

    public static String[] getHeadAndBody(String eventRaw) {
        int index = eventRaw.indexOf("|");
        if (index < 0) {
            log.error("[Event Parse] string format incorrect!");
            return null;
        }
        String[] events = new String[2];
        events[0] = eventRaw.substring(0, index);
        events[1] = eventRaw.substring(index + 1);
        return events;
    }

}
