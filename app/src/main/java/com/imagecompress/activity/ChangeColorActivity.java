package com.imagecompress.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.imagecompress.R;
import com.imagecompress.utils.ImageHelper;

import java.net.URI;
//https://blog.csdn.net/cfy137000/article/details/54646912

public class ChangeColorActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    private ImageView mChangeColorIv;
    private SeekBar mHueSeekBar, mSaturationSeekBar, mLumSeekBar;
    private Button mChooseButton;

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
        onClickButtonListener();

        //获得图片资源
//        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ayaki);
//        mChangeColorIv.setImageBitmap(mBitmap);

        //对seekBar设置监听
        mHueSeekBar.setOnSeekBarChangeListener(this);
        mSaturationSeekBar.setOnSeekBarChangeListener(this);
        mLumSeekBar.setOnSeekBarChangeListener(this);

    }

    public void onClickButtonListener() {
        mChooseButton = (Button) findViewById(R.id.choose_btn);
        mChooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 2);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && null != data) {
            Uri uri = data.getData();
            ContentResolver cr = this.getContentResolver();
            try {
                mBitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                mChangeColorIv.setImageBitmap(mBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

