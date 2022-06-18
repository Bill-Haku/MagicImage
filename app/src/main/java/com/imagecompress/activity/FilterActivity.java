package com.imagecompress.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
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
import java.util.Random;
//https://blog.csdn.net/cfy137000/article/details/54646912

public class FilterActivity extends AppCompatActivity {
    private ImageView mChangeColorIv;
    private Button mChooseButton, mSaveButton;
    private Button mOldTimeFilter, mBlackWhiteFilter, mAnimeFilter;

    private Bitmap mBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        mChangeColorIv = (ImageView) findViewById(R.id.change_color_iv);
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
                    String path = Environment.getExternalStorageDirectory().getPath() + "/DCIM/magicimage/";
                    File temp = new File(path);
                    if (!temp.exists()) {
                        temp.mkdirs();
                    }
                    Random random = new Random();
                    int randNum = random.nextInt(99999999);
                    String randStr = String.valueOf(randNum);
                    String fileName = path + "img_" + randStr + ".jpg";
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
                else {
                    Toast.makeText(getApplicationContext(), "请先选择图片", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mOldTimeFilter = (Button) findViewById(R.id.old_time_btn);
        mOldTimeFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBitmap != null) {
                    oldTimeFilter();
                }
                else {
                    Toast.makeText(getApplicationContext(), "请先选择图片", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mBlackWhiteFilter = (Button) findViewById(R.id.black_white_btn);
        mBlackWhiteFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBitmap != null) {
                    blackWhiteFilter();
                }
                else {
                    Toast.makeText(getApplicationContext(), "请先选择图片", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mAnimeFilter = (Button) findViewById(R.id.anime_btn);
        mAnimeFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBitmap != null) {
                    AnimeFilter();
                }
                else {
                    Toast.makeText(getApplicationContext(), "请先选择图片", Toast.LENGTH_SHORT).show();
                }
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

    public void oldTimeFilter() {
        //直接操作矩阵 来改变图片的风格(加滤镜);
        Bitmap bmp = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        //操作矩阵
        ColorMatrix colorMatrix = new ColorMatrix(new float[]{
                0.393f, 0.769f, 0.189f, 0, 0,
                0.349f, 0.686f, 0.168f, 0, 0,
                0.272f, 0.534f, 0.131f, 0, 0,
                0, 0, 0, 1, 0
        });
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(mBitmap, 0, 0, paint);
        mChangeColorIv.setImageBitmap(bmp);
    }

    public void blackWhiteFilter() {
        //直接操作矩阵 来改变图片的风格(加滤镜);
        Bitmap bmp = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        //操作矩阵
        ColorMatrix colorMatrix = new ColorMatrix(new float[]{
                0.33f, 0.59f, 0.11f, 0, 0,
                0.33f, 0.59f, 0.11f, 0, 0,
                0.33f, 0.59f, 0.11f, 0, 0,
                0, 0, 0, 1, 0
        });
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(mBitmap, 0, 0, paint);
        mChangeColorIv.setImageBitmap(bmp);
    }

    public void AnimeFilter() {
        //直接操作矩阵 来改变图片的风格(加滤镜);
        Bitmap bmp = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        //操作矩阵
        ColorMatrix colorMatrix = new ColorMatrix(new float[]{
                1.438f, -0.122f, -0.016f, 0, -0.03f,
                -0.062f, 1.378f, -0.016f, 0, 0.05f,
                -0.062f, -0.122f, 1.483f, 0, -0.02f,
                0, 0, 0, 1, 0
        });
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(mBitmap, 0, 0, paint);
        mChangeColorIv.setImageBitmap(bmp);
    }
}

