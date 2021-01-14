package com.yuugu.modular.dispatcher.core;

import android.util.Log;

public class L {

    private static boolean ENABLE = false;

    private L() {

    }

    public static void setEnabled(boolean enabled) {
        ENABLE = enabled;
    }

    public static void i(String tag, String msg) {
        if (!ENABLE) return;
        Log.i(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (!ENABLE) return;
        Log.e(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (!ENABLE) return;
        Log.w(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (!ENABLE) return;
        Log.d(tag, msg);
    }
}

