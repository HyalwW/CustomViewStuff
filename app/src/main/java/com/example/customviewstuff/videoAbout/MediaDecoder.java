package com.example.customviewstuff.videoAbout;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import java.io.File;

/**
 * Created by Wang.Wenhui
 * Date: 2020/1/14
 */
public class MediaDecoder {
    private static final String TAG = "MediaDecoder";
    private MediaMetadataRetriever retriever;
    private String fileLength;
    private boolean isDone;

    public MediaDecoder() {
        retriever = new MediaMetadataRetriever();
    }

    public void decore(String file) {
        if (isFileExists(new File(file))) {
            isDone = false;
            retriever.setDataSource(file);
            fileLength = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            Log.i(TAG, "fileLength : " + fileLength);
            isDone = true;
        }
    }

    /**
     * 获取视频某一帧
     *
     * @param timeMs 毫秒
     */
    public Bitmap decodeFrame(long timeMs) {
        if (retriever == null) return null;
        Bitmap bitmap = retriever.getFrameAtTime(timeMs * 1000, MediaMetadataRetriever.OPTION_CLOSEST);
        if (bitmap == null) return null;
        return bitmap;
    }

    /**
     * 取得视频文件播放长度
     *
     * @return
     */
    public long getVedioFileLength() {
        return Long.parseLong(fileLength);
    }

    private static boolean isFileExists(File file) {
        return file != null && file.exists();
    }

    public boolean isDone() {
        return isDone;
    }

}
