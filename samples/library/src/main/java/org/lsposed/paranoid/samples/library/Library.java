package org.lsposed.paranoid.samples.library;

import android.util.Log;

public class Library {
    public static String TAG = "Library";

    public static void log() {
        Log.d(TAG, "not obfuscated");
    }
}
