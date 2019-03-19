package com.example.administrator.easycure.utils;

import android.graphics.Color;

/**
 * Created by Administrator on 2019/3/17 0017.
 */

public class RandomColor {

    public static final int[] colors = {0xFF000000,0xFFF4A460,0xFFFF4500,0xFFEE00EE,0xFFA9A9A9,0xFF00B2EE,0xFF20B2AA,0xFF104E8B};

    public static int getRamdomColor(){
        return colors[(int)Math.round(Math.random()*7)];
    }
}