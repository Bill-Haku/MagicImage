package com.imagecompress.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.imagecompress.R;
import com.imagecompress.utils.ImageHelper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Random;
//https://blog.csdn.net/cfy137000/article/details/54646912

public class ChangeColorActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    private ImageView mChangeColorIv;
    private SeekBar mHueSeekBar, mSaturationSeekBar, mLumSeekBar;
    private Button mChooseButton, mSaveButton;

    private Bitmap mBitmap = null;

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
        mChooseButton = (Button) findViewById(R.id.choose_btn);
        mChooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 2);
            }
        });
        mSaveButton = (Button) findViewById(R.id.save_btn);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBitmap != null) {
                    mChangeColorIv.buildDrawingCache(true);
                    mChangeColorIv.buildDrawingCache();
                    Bitmap saveBitmap = mChangeColorIv.getDrawingCache();
                    File temp = new File("/sdcard/DCIM/image");
                    if (!temp.exists()) {
                        temp.mkdirs();
                    }
                    Random random = new Random();
                    int randNum = random.nextInt(99999999);
                    String randStr = String.valueOf(randNum);
                    String fileName = Environment.getExternalStorageDirectory().getPath() +  "/DCIM/magicimage/" +  randStr + ".jpg";
                    File file = new File(fileName);
                    try {
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                        saveBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                        bos.flush();
                        bos.close();
                        Toast.makeText(getApplicationContext(), "图片已保存到[ExternalStorageDirectory]/DCIM/magicimage/", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //获得图片资源
//        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ayaki);
//        mChangeColorIv.setImageBitmap(mBitmap);

        //对seekBar设置监听
        mHueSeekBar.setOnSeekBarChangeListener(this);
        mSaturationSeekBar.setOnSeekBarChangeListener(this);
        mLumSeekBar.setOnSeekBarChangeListener(this);

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

        if (mBitmap != null) {
            Bitmap bitmap = ImageHelper.getChangedBitmap(mBitmap, mHue, mSaturation, mLum);
            mChangeColorIv.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}

