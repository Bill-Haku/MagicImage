package com.imagecompress.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.app.SharedElementCallback;

import com.bumptech.glide.Glide;
import com.imagecompress.R;
import com.imagecompress.api.Contast;
import com.imagecompress.base.BaseActivity;
import com.imagecompress.bean.ImageFileBean;
import com.imagecompress.utils.PairHelp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import utils.FileUtils;
import utils.LogUtils;
import utils.bean.ImageConfig;
import utils.task.AsyncImageSharpeningTask;
import utils.task.AsyncImageTask;
import utils.task.SharpeningImageTask;

public class SharpeningActivity extends BaseActivity {
    private ImageView mImageView, mSharpeningImageView;
    private TextView mRawText, mSharpeningText;
    private View mChooseView, mSharpeningView, mEdgeHighlightView, mSmoothView, mGaussSmoothView;
    private File mImageFile;
    // private boolean mIsSharpening;

    private int mClickPosition;

    private File mSharpeningImageFile;

    private List<String> mFilePathData;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sharpening_image;
    }

    @Override
    protected void initView() {
        mImageView = findViewById(R.id.raw_iv);
        mSharpeningImageView = findViewById(R.id.sharpening_iv);
        mRawText = findViewById(R.id.raw_tv);
        mSharpeningText = findViewById(R.id.sharpening_tv);
        mChooseView = findViewById(R.id.choose_btn);
        mSharpeningView = findViewById(R.id.sharpening_btn);
        mEdgeHighlightView = findViewById(R.id.edge_highlight_btn);
        mSmoothView = findViewById(R.id.smoothing_btn);
        mGaussSmoothView = findViewById(R.id.gauss_smooth_btn);
    }


    @Override
    protected void initData() {
        mFilePathData = new ArrayList<>();
    }


    @Override
    protected void initEvent() {
        mChooseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickChooseView();
                openPhoto(true);
            }
        });
        mSharpeningView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSharpeningView(1);
            }
        });
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickPosition = 0;
                clickRawImage(view);
            }
        });
        mSharpeningImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickPosition = 1;
                clickSharpeningImage(view);
            }
        });
        mEdgeHighlightView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSharpeningView(2);
            }
        });
        mSmoothView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSharpeningView(3);
            }
        });
        mGaussSmoothView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSharpeningView(4);
            }
        });

        ActivityCompat.setExitSharedElementCallback(this, new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                super.onMapSharedElements(names, sharedElements);
                switch (PairHelp.PREVIEW_POSITION) {
                    case 0:
                        sharedElements.put(PairHelp.transitionName(), mImageView);
                        break;
                    case 1:
                        sharedElements.put(PairHelp.transitionName(), mSharpeningImageView);
                        break;
                }
            }
        });
    }

    private void clickSharpeningImage(View view) {
        PairHelp.setPreviewPosition(1);
        toPreviewActivity(view, mSharpeningImageView, mSharpeningImageFile);
    }


    private void clickRawImage(View view) {
        PairHelp.setPreviewPosition(0);
        toPreviewActivity(view, mImageView, mImageFile);
    }

    private void toPreviewActivity(View view, ImageView imageView, File imageFile) {
        if (imageView.getDrawable() != null && FileUtils.isImageFile(imageFile)) {
            Intent intent = new Intent(this, PreviewImageActivity.class);
            intent.putStringArrayListExtra(Contast.IMAGE_PATH_KEY, (ArrayList<String>) mFilePathData);
            intent.putExtra(Contast.CLICK_IMAGE_POSITION_KEY, mClickPosition);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this
                        , PairHelp.addPair(view)).toBundle();
                startActivity(intent, bundle);
            } else {
                startActivity(intent);
            }
        }
    }

    private void clickSharpeningView(int type) {
        if (mImageFile == null) {
            Toast.makeText(getApplicationContext(), "请先选择图片", Toast.LENGTH_SHORT).show();
        } else {
            if (FileUtils.isImageFile(mImageFile)) {
                if (!SharpeningImageTask.get().isSharpeningImage()) {
                    final ViewGroup viewGroup = (ViewGroup) getWindow().getDecorView();
                    final View inflate = LayoutInflater.from(SharpeningActivity.this).inflate(R.layout.item_loading_view, viewGroup, false);

                    SharpeningImageTask.get().sharpeningImage(ImageConfig.getDefaultConfig(mImageFile.getAbsolutePath(),
                            FileUtils.resultImageFile(FileUtils.outFileDirectory(SharpeningActivity.this)).getAbsolutePath()), new AsyncImageSharpeningTask.OnImageResult() {
                        @Override
                        public void startSharpening() {
                            viewGroup.addView(inflate);
                        }

                        @Override
                        public void resultFileSucceed(File file) {
                            mSharpeningImageFile = file;
                            mFilePathData.add(file.getAbsolutePath());
                            if (!SharpeningActivity.this.isFinishing()) {
                                Glide.with(SharpeningActivity.this).load(file).into(mSharpeningImageView);
                            }
                            mSharpeningText.setText("Size:" + FileUtils.imageSize(file.length()));
                            if (viewGroup.indexOfChild(inflate) != -1) {
                                viewGroup.removeView(inflate);
                            }
                            LogUtils.w("mImageFile--", file.getAbsolutePath());
                        }

                        @Override
                        public void resultError() {
                            if (viewGroup.indexOfChild(inflate) != -1) {
                                viewGroup.removeView(inflate);
                            }
                        }
                    }, type);
                } else {
                    Toast.makeText(getApplicationContext(), "正在处理中，请稍等", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "该文件不是图片", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void clickChooseView() {
        if (mImageView.getDrawable() != null) {
            mImageView.setImageDrawable(null);
        }
        if (mImageFile != null) {
            mRawText.setText(null);
            mImageFile = null;
        }
        if (mSharpeningImageFile != null) {
            mSharpeningText.setText(null);
        }
        if (mSharpeningImageView.getDrawable() != null) {
            mSharpeningImageView.setImageDrawable(null);
        }
        mFilePathData.clear();

    }


    @Override
    protected void imageFileResult(ImageFileBean bean) {
        super.imageFileResult(bean);
        if (bean != null) {
            mImageFile = bean.imageFile;
            mFilePathData.add(bean.imageFile.getAbsolutePath());
            Glide.with(this).load(bean.imageFile).into(mImageView);
//            mRawText.setText("Size:" + FileUtils.imageSize(mImageFile.length()));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
