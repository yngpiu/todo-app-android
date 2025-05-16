package com.haui.noteapp.util;

import android.graphics.Color;

import java.util.Random;

public class ColorUtil {
    public static String getRandomColor() {
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        return String.format("#%06X", (0xFFFFFF & color));
    }
}
