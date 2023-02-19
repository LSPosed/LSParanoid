package org.lsposed.paranoid.samples.library_may_obfuscate;

import android.util.Log;

public class LibraryMayNotObfuscate {
    public static String TAG = "LibraryMayNotObfuscate";

    public static void log() {
        Log.d(TAG, "may not obfuscated");
    }
}
