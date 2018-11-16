package com.marcouberti.caregivers.util;

import android.graphics.Color;

import java.util.HashMap;

public class ColorUtils {

    private static HashMap<String, String> colors = new HashMap<>();

    static {
        colors.put("1",     "#FFC974");
        colors.put("2",     "#E06A83");
        colors.put("3",     "#7BB472");
        colors.put("4",     "#5B7BA5");
        colors.put("5",     "#E49770");
        colors.put("6",     "#C46262");
        colors.put("7",     "#618BAF");
        colors.put("8",     "#E2D071");
        colors.put("9",     "#3CC492");
        colors.put("10",    "#335870");
    }


    public static int getRoomColor(String roomId) {
        if(colors.containsKey(roomId)) return Color.parseColor(colors.get(roomId));
        else return Color.GREEN;
    }
}
