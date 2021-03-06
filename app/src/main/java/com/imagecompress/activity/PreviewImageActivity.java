package com.imagecompress.activity;

import android.content.Intent;
import android.os.Build;
import androidx.viewpager.widget.ViewPager;
import android.view.Window;
import android.view.WindowManager;

import com.imagecompress.R;
import com.imagecompress.adapter.PreviewAdapter;
import com.imagecompress.api.Contast;
import com.imagecompress.base.BaseActivity;
import com.imagecompress.utils.PairHelp;
import com.imagecompress.weight.PreviewPager;

import java.util.List;


public class PreviewImageActivity extends BaseActivity {

    private PreviewAdapter mAdapter;
    private PreviewPager mViewPager;

    @Override
    protected int getLayoutId() {
        return R.layout.item_preview_pager;
    }

    @Override
    protected void initView() {
        mViewPager = findViewById(R.id.vp);

    }


    @Override
    protected void initUI() {
        super.initUI();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            if (window == null) {
                finish();
                return;
            }
          window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            List<String> imagePathList = intent.getStringArrayListExtra(Contast.IMAGE_PATH_KEY);
            if (imagePathList != null && imagePathList.size() > 0) {
                if (mAdapter == null){
                    mAdapter = new PreviewAdapter(imagePathList,this);
                    mViewPager.setOffscreenPageLimit(0);
                    mViewPager.setAdapter(mAdapter);
                    mViewPager.setCurrentItem( intent.getIntExtra(Contast.CLICK_IMAGE_POSITION_KEY,0),true);
                    mViewPager.setPageTransformer(true,new PreviewAdapter.PreviewPageTransformer());
                    mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                        }

                        @Override
                        public void onPageSelected(int position) {
                            PairHelp.setPreviewPosition(mViewPager.getCurrentItem());
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });
                }else {
                    mAdapter.notifyDataSetChanged();
                }

            } else {
                finish();
            }
        }
    }



    @Override
    protected void initEvent() {

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();


    }
}
