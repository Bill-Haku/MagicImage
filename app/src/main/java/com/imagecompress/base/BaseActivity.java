package com.imagecompress.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.WindowManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import utils.task.ActivityPicker;



public abstract class BaseActivity extends CameraActivity {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ActivityPicker.get().addActivity(this);
        initUI();
        initPermission();


    }

    protected void initPermission() {
        initView();
        initData();
        initEvent();

    }


    protected void initUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager sbt = new SystemBarTintManager(this);
            sbt.setStatusBarTintColor(Color.TRANSPARENT);
            sbt.setStatusBarTintEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        ActivityPicker.get().removeActivity(getClass().getSimpleName());
        super.onDestroy();

    }

    protected abstract int getLayoutId();


    protected abstract void initView();

    protected abstract void initData();

    protected abstract void initEvent();


}
