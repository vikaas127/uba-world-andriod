package com.ubaworld.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.zhihu.matisse.Matisse;

public class Picture {

    public static final int REQUEST_CAMERA = 8881;
    public static final int REQUEST_CODE_CHOOSE = 10555;

    private static String mCurrentPhotoPath;

    private Activity activity;

    public Picture(Activity activity) {
        this.activity = activity;
    }

    public void result(int requestCode, int resultCode, Intent data, OnPictureUpdate onPictureUpdate) {
        if (requestCode == Picture.REQUEST_CODE_CHOOSE && resultCode == Activity.RESULT_OK) {
            Uri uri = Matisse.obtainResult(data).get(0);
            data.setData(uri);
            if (data != null && data.getData() != null)
                LogUtils.e("DATA ", data.getData().toString());
            onPictureUpdate.onUpdatePhoto(data.getData());
        }
    }

    public static String getCurrentPhoto() {
        return mCurrentPhotoPath;
    }

    public interface OnPictureUpdate {
        void onUpdatePhoto(Uri data);
    }

}
