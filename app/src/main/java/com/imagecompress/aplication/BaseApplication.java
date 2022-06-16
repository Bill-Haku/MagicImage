package com.imagecompress.aplication;

import androidx.multidex.MultiDexApplication;

import com.imagecompress.toast.Toasts;

public class BaseApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        init();

    }

    private void init() {
      /*  if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);*/

        Toasts.init(this);
       // UiUtils.init(this);

    }
}
