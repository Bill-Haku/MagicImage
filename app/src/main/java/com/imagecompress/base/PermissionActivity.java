package com.imagecompress.base;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.imagecompress.permission.IntentUtils;
import com.imagecompress.permission.imp.OnPermissionsResult;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



@SuppressLint("Registered")
public class PermissionActivity extends RxAppCompatActivity {
    private AlertDialog mForbidDialog;
    private static final int REQUEST_CODE = 100;
    private  List<String> mAllowList = new ArrayList<>();
    private  List<String> mNoAllowList = new ArrayList<>();
    private  List<String> mForbidList = new ArrayList<>();
    private OnPermissionsResult mOnPermissionsResult;
    private String[] mPermissions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    protected void requestPermission(@NonNull OnPermissionsResult onPermissionsResult, @NonNull String... permissions) {
        this.mPermissions = permissions;
        this.mOnPermissionsResult = onPermissionsResult;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> requestList = new ArrayList<>();
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    requestList.add(permission);
                }
            }
            if (requestList.size() > 0) {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);
            } else {
                if (mOnPermissionsResult != null ) {
                    mOnPermissionsResult.onAllow(Arrays.asList(permissions));
                }
            }
        } else {
            mOnPermissionsResult.onLowVersion();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                if (permissions.length == grantResults.length) {
                    clearPermission();

                    for (int i = 0; i < grantResults.length; i++) {

                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            mAllowList.add(permissions[i]);
                        } else {
                            Log.w("onRequemt--", ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]) + "");
                            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                                mNoAllowList.add(permissions[i]);
                            } else {
                                mForbidList.add(permissions[i]);
                            }
                        }
                    }
                    Log.w("onRequest--", permissions.length + "--" + grantResults.length
                            + "--" + mAllowList.size() + "--" + mNoAllowList.size() + "--" + mForbidList.size());
                    if (mAllowList.size() == permissions.length) {
                        if (mOnPermissionsResult != null) {
                            //????????????
                            mOnPermissionsResult.onAllow(mAllowList);
                        }

                    } else {
                        if (mForbidList.size() > 0) {
                            if (mOnPermissionsResult != null) {
                                //??????????????????????????????????????????
                                mOnPermissionsResult.onForbid(mForbidList);
                            }
                        } else {
                            if (mOnPermissionsResult != null) {
                                //????????????
                                mOnPermissionsResult.onNoAllow(mNoAllowList);
                            }
                        }
                    }

                }
                break;
            default:
                break;
        }
    }

    private void clearPermission() {
        mAllowList.clear();
        mNoAllowList.clear();
        mForbidList.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearPermission();
    }

    protected void showForbidPermissionDialog(@NonNull String name) {
        if (mForbidDialog == null) {
            mForbidDialog = new AlertDialog.Builder(this).setTitle("????????????" + name + "??????")
                    .setMessage("????????????" + name + "???????????????????????????????????????????????????????????????-??????-ImageCompress-??????")
                    .setPositiveButton("??????", null)
                    .setNegativeButton("??????", null)
                    .setCancelable(false).create();
            mForbidDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button positionButton = mForbidDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    Button negativeButton = mForbidDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                    positionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            IntentUtils.openActivityApplyCenter(PermissionActivity.this);

                        }
                    });
                    negativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            mForbidDialog.dismiss();
                        }
                    });

                }
            });
            mForbidDialog.show();
        } else if (!mForbidDialog.isShowing()) {
            mForbidDialog.show();
        }
    }

    protected void dismissForbidPermissionDialog() {
        if (mForbidDialog != null && mForbidDialog.isShowing()) {
            mForbidDialog.dismiss();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.w("FragmentResult--", requestCode + "--" + resultCode);
        if (requestCode == IntentUtils.OPEN_APPLY_CENTER_CODE && resultCode == 0) {
            if (mPermissions != null && mPermissions.length > 0 && mOnPermissionsResult != null) {
                requestPermission(mOnPermissionsResult, mPermissions);
                dismissForbidPermissionDialog();
                // getActivity().recreate();
            }
        }
    }
}
