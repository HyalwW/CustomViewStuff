package com.example.customviewstuff.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.customviewstuff.BaseActivity;
import com.example.customviewstuff.R;
import com.example.customviewstuff.databinding.ActivityPttBinding;
import com.example.customviewstuff.helpers.FileUtil;
import com.zhy.base.fileprovider.FileProvider7;

import java.io.File;

public class PTTActivity extends BaseActivity<ActivityPttBinding> implements View.OnClickListener {
    private String mTempPhotoPath;

    @Override
    protected int layoutId() {
        return R.layout.activity_ptt;
    }

    @Override
    protected void onInit() {
        new Thread(() -> dataBinding.pttView.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.gm))).start();
        dataBinding.choosePic.setOnClickListener(this);
        dataBinding.takePhoto.setOnClickListener(this);
        dataBinding.draw.setOnClickListener(this);
    }

    private void choosePhoto() {
        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intentToPickPic, 1);
    }

    private void takePhoto() {
        Intent intentToTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File fileDir = new File(Environment.getExternalStorageDirectory() + File.separator + "photoTest" + File.separator);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        File photoFile = new File(fileDir, "photo" + System.currentTimeMillis() + ".jpeg");
        mTempPhotoPath = photoFile.getAbsolutePath();
        Uri imageUri = FileProvider7.getUriForFile(this, photoFile);
        intentToTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intentToTakePhoto, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap;
        switch (requestCode) {
            case 1:
                if (data != null && data.getData() != null) {
                    Uri uri = data.getData();
                    String filePath = FileUtil.getFilePathByUri(this, uri);
                    bitmap = BitmapFactory.decodeFile(filePath);
                    Log.e("wwh", "MainActivity-->onActivityResult(): ");
                    dataBinding.pttView.setBitmap(bitmap);
                } else {
                    toast("选择的图片不存在");
                }
                break;
            case 2:
                if (!TextUtils.isEmpty(mTempPhotoPath)) {
                    bitmap = BitmapFactory.decodeFile(mTempPhotoPath);
                    if (bitmap != null) {
                        dataBinding.pttView.setBitmap(bitmap);
                    } else {
                        toast("拍照失败2");
                    }
                } else {
                    toast("拍照失败1");
                }
                break;
        }
    }

    private void toast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.choose_pic:
                choosePhoto();
                break;
            case R.id.take_photo:
                takePhoto();
                break;
            case R.id.draw:
                dataBinding.pttView.draw();
                break;
        }
    }
}
