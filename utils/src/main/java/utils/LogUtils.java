package utils;

import android.util.Log;
// import com.imagecompress.*;



public class LogUtils {
    private static final boolean isDebug = true;

    public static void w(String TAG, String msg) {
        if (isDebug) {
            Log.w(TAG, msg);
        }
    }

    public static void d(String TAG, String msg) {
        if (isDebug) {
            Log.d(TAG, msg);
        }
    }
}
