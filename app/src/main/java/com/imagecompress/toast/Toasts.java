package com.imagecompress.toast;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import android.widget.Toast;


public class Toasts {
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private @SuppressLint("ShowToast")
    static Toast mToast;

    private Toasts() {
    }

    public static void init(Context context) {
        Toasts.mContext = context;
    }

    @SuppressLint("ShowToast")
    public static void show(@NonNull String text) {
        synchronized (Toasts.class) {
            if (mToast == null) {
                synchronized (Toasts.class) {
                    mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
                }
            } else {
                mToast.setText(text);
            }
        }
        mToast.show();
    }
}
