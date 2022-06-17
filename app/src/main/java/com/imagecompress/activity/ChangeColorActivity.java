package com.imagecompress.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;
import androidx.appcompat.app.AppCompatActivity;

import com.imagecompress.R;
import com.imagecompress.utils.ImageHelper;

public class ChangeColorActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    private ImageView mChangeColorIv;
    private SeekBar mHueSeekBar, mSaturationSeekBar, mLumSeekBar;

    private Bitmap mBitmap;

    private float mHue = 0, mSaturation = 1f, mLum = 1f;
    private static final int MID_VALUE = 128;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_color);

        mChangeColorIv = (ImageView) findViewById(R.id.change_color_iv);
        mHueSeekBar = (SeekBar) findViewById(R.id.hue_seek_bar);
        mSaturationSeekBar = (SeekBar) findViewById(R.id.saturation_seek_bar);
        mLumSeekBar = (SeekBar) findViewById(R.id.lum_seek_bar);

        //获得图片资源
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ayaki);
        mChangeColorIv.setImageBitmap(mBitmap);

        //对seekBar设置监听
        mHueSeekBar.setOnSeekBarChangeListener(this);
        mSaturationSeekBar.setOnSeekBarChangeListener(this);
        mLumSeekBar.setOnSeekBarChangeListener(this);


    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.hue_seek_bar:
                //色相的范围是正负180
                mHue = (progress - MID_VALUE) * 1f / MID_VALUE * 180;
                break;
            case R.id.saturation_seek_bar:
                //范围是0-2;
                mSaturation = progress * 1f / MID_VALUE;
                break;
            case R.id.lum_seek_bar:
                mLum = progress * 1f / MID_VALUE;
                break;
        }

        Bitmap bitmap = ImageHelper.getChangedBitmap(mBitmap,
                mHue, mSaturation, mLum);
        mChangeColorIv.setImageBitmap(bitmap);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}

