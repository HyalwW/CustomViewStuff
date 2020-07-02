package com.example.customviewstuff.bitmapViewer;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.example.customviewstuff.BaseActivity;
import com.example.customviewstuff.R;
import com.example.customviewstuff.ThreadPool;
import com.example.customviewstuff.databinding.ActivityBitmapViewBinding;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BitmapViewActivity extends BaseActivity<ActivityBitmapViewBinding> {

    @Override
    protected int layoutId() {
        return R.layout.activity_bitmap_view;
    }

    @Override
    protected void onInit() {
        getDocumentData();
    }

    public void getDocumentData() {
        ThreadPool.cache().execute(() -> {
            String[] columns = new String[]{MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.MIME_TYPE, MediaStore.Files.FileColumns.SIZE, MediaStore.Files.FileColumns.DATE_MODIFIED, MediaStore.Files.FileColumns.DATA};
            String select = "(_data LIKE '%.pdf')";
            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = contentResolver.query(MediaStore.Files.getContentUri("external"), columns, select, null, null);
            int columnIndexOrThrow_DATA = 0;
            if (cursor != null) {
                String[] strings = new String[cursor.getCount()];
                columnIndexOrThrow_DATA = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
                int index = 0;
                while (cursor.moveToNext()) {
                    String path = cursor.getString(columnIndexOrThrow_DATA);
                    strings[index++] = path;
                }
                cursor.close();
                if (strings.length > 0) {
                    dataBinding.getRoot().post(() -> new AlertDialog.Builder(this)
                            .setTitle("选择文件")
                            .setItems(strings, (dialog, which) -> {
                                String fileName = strings[which];
                                try {
                                    List<Bitmap> ll = new ArrayList<>();
                                    PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(new File(fileName), ParcelFileDescriptor.MODE_READ_ONLY));
                                    int pageCount = renderer.getPageCount();
                                    renderer.close();
                                    for (int i = 0; i < pageCount; i++) {
                                        ll.add(null);
                                    }
                                    MyAdapter adapter = new MyAdapter(this, ll, fileName);
                                    dataBinding.listView.setAdapter(adapter);
                                    dataBinding.listView.setLayoutManager(new LinearLayoutManager(this));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                dialog.dismiss();
                            })
                            .create()
                            .show());
                }
            }
        });

    }
}
