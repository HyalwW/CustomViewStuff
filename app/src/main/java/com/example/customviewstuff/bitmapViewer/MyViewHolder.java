package com.example.customviewstuff.bitmapViewer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.LruCache;
import android.view.View;

import com.example.customviewstuff.AbsViewHolder;
import com.example.customviewstuff.R;
import com.example.customviewstuff.ThreadPool;
import com.example.customviewstuff.databinding.ItemBitmapBinding;

import java.io.File;
import java.util.Locale;

/**
 * Created by Wang.Wenhui
 * Date: 2020/6/24
 * Description: blablabla
 */
public class MyViewHolder extends AbsViewHolder<Bitmap, ItemBitmapBinding> {
    private LruCache<String, Bitmap> cache;

    public MyViewHolder(@NonNull View itemView, LruCache<String, Bitmap> cache) {
        super(itemView);
        this.cache = cache;
    }

    @Override
    protected void initView(View itemView) {

    }

    @Override
    public void bind(Bitmap item, int position, int size) {
        if (BitmapViewActivity.needCache) {
            ThreadPool.cache().execute(() -> {
                Bitmap bitmap = cache.get("position" + position);
                if (bitmap == null || bitmap.isRecycled()) {
                    bitmap = getBitmap(new File(BitmapViewActivity.fileName), position);
//                    bitmap = BitmapFactory.decodeResource(dataBinding.getRoot().getResources(), R.drawable.gm);
                    cache.put("position" + position, bitmap);
                }
                dataBinding.image.setBitmap(bitmap, false);
            });
        } else {
            dataBinding.image.setBitmap(R.drawable.gm, true);
        }
        dataBinding.serial.setText(String.format(Locale.CHINA, "%d/%d", position + 1, size));
    }

    private Bitmap getBitmap(File pdfFile, int p) {
        Bitmap bitmap = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY));
                PdfRenderer.Page page = renderer.openPage(p);
                int width = dataBinding.getRoot().getResources().getDisplayMetrics().densityDpi / 72 * page.getWidth();
                int height = dataBinding.getRoot().getResources().getDisplayMetrics().densityDpi / 72 * page.getHeight();
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Rect r = new Rect(0, 0, width, height);
                page.render(bitmap, r, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                page.close();
                renderer.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return bitmap;

    }
}
