package com.example.customviewstuff.activities;

import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.customviewstuff.BaseActivity;
import com.example.customviewstuff.R;
import com.example.customviewstuff.customs.VideoView;
import com.example.customviewstuff.databinding.ActivityVideoBinding;
import com.example.customviewstuff.videoAbout.FilmAdapter;
import com.example.customviewstuff.videoAbout.VideoInfo;

import java.util.ArrayList;
import java.util.List;

public class VideoActivity extends BaseActivity<ActivityVideoBinding> implements View.OnClickListener {
    private static final String[] sLocalVideoColumns = {
            MediaStore.Video.Media._ID, // 视频id
            MediaStore.Video.Media.DATA, // 视频路径
            MediaStore.Video.Media.SIZE, // 视频字节大小
            MediaStore.Video.Media.DISPLAY_NAME, // 视频名称 xxx.mp4
            MediaStore.Video.Media.TITLE, // 视频标题
            MediaStore.Video.Media.DATE_ADDED, // 视频添加到MediaProvider的时间
            MediaStore.Video.Media.DATE_MODIFIED, // 上次修改时间，该列用于内部MediaScanner扫描，外部不要修改
            MediaStore.Video.Media.MIME_TYPE, // 视频类型 video/mp4
            MediaStore.Video.Media.DURATION, // 视频时长
            MediaStore.Video.Media.ARTIST, // 艺人名称
            MediaStore.Video.Media.ALBUM, // 艺人专辑名称
            MediaStore.Video.Media.RESOLUTION, // 视频分辨率 X x Y格式
            MediaStore.Video.Media.DESCRIPTION, // 视频描述
            MediaStore.Video.Media.IS_PRIVATE,
            MediaStore.Video.Media.TAGS,
            MediaStore.Video.Media.CATEGORY, // YouTube类别
            MediaStore.Video.Media.LANGUAGE, // 视频使用语言
            MediaStore.Video.Media.LATITUDE, // 拍下该视频时的纬度
            MediaStore.Video.Media.LONGITUDE, // 拍下该视频时的经度
            MediaStore.Video.Media.DATE_TAKEN,
            MediaStore.Video.Media.MINI_THUMB_MAGIC,
            MediaStore.Video.Media.BUCKET_ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.BOOKMARK // 上次视频播放的位置
    };
    private static final String[] sLocalVideoThumbnailColumns = {
            MediaStore.Video.Thumbnails.DATA, // 视频缩略图路径
            MediaStore.Video.Thumbnails.VIDEO_ID, // 视频id
            MediaStore.Video.Thumbnails.KIND,
            MediaStore.Video.Thumbnails.WIDTH, // 视频缩略图宽度
            MediaStore.Video.Thumbnails.HEIGHT // 视频缩略图高度
    };
    private List<VideoInfo> mVideoInfos;
    private FilmAdapter adapter;
    private AlertDialog dialog;


    @Override
    protected int layoutId() {
        return R.layout.activity_video;
    }

    @Override
    protected void onInit() {
        mVideoInfos = new ArrayList<>();
        initView();
        initFilms();
    }

    private void initView() {
        adapter = new FilmAdapter(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog, null);
        dialog = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(true)
                .create();
        ListView listView = view.findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, v, position, id) -> {
            dialog.dismiss();
            dataBinding.video.setFile(mVideoInfos.get(position).data);
        });
        dataBinding.seekBar.setMax(100);

        dataBinding.video.setListener(new VideoView.PlayerListener() {
            @Override
            public void onProgressBuffered(int progress) {
                dataBinding.seekBar.setSecondaryProgress(progress);
            }

            @Override
            public void onProgressPlayed(int progress) {
                dataBinding.seekBar.setProgress(progress);
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onStop() {

            }
        });
        dataBinding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //todo 延迟高，优化后再用
//                if (fromUser) {
//                    videoView.seekTo(progress);
//                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                dataBinding.video.seekTo(dataBinding.seekBar.getProgress());
            }
        });
        dataBinding.start.setOnClickListener(this);
        dataBinding.chooseVideo.setOnClickListener(this);
        dataBinding.pause.setOnClickListener(this);
    }

    private void initFilms() {
        new Thread(() -> {
            Cursor cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, sLocalVideoColumns,
                    null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    VideoInfo videoInfo = new VideoInfo();

                    int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                    String data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                    long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                    String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                    long dateAdded = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));
                    long dateModified = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED));
                    String mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE));
                    long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.ARTIST));
                    String album = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.ALBUM));
                    String resolution = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.RESOLUTION));
                    String description = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DESCRIPTION));
                    int isPrivate = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.IS_PRIVATE));
                    String tags = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TAGS));
                    String category = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.CATEGORY));
                    double latitude = cursor.getDouble(cursor.getColumnIndex(MediaStore.Video.Media.LATITUDE));
                    double longitude = cursor.getDouble(cursor.getColumnIndex(MediaStore.Video.Media.LONGITUDE));
                    int dateTaken = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN));
                    int miniThumbMagic = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.MINI_THUMB_MAGIC));
                    String bucketId = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID));
                    String bucketDisplayName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME));
                    int bookmark = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.BOOKMARK));

                    Cursor thumbnailCursor = getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, sLocalVideoThumbnailColumns,
                            MediaStore.Video.Thumbnails.VIDEO_ID + "=" + id, null, null);
                    if (thumbnailCursor != null && thumbnailCursor.moveToFirst()) {
                        do {
                            String thumbnailData = thumbnailCursor.getString(thumbnailCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                            int kind = thumbnailCursor.getInt(thumbnailCursor.getColumnIndex(MediaStore.Video.Thumbnails.KIND));
                            long width = thumbnailCursor.getLong(thumbnailCursor.getColumnIndex(MediaStore.Video.Thumbnails.WIDTH));
                            long height = thumbnailCursor.getLong(thumbnailCursor.getColumnIndex(MediaStore.Video.Thumbnails.HEIGHT));

                            videoInfo.thumbnailData = thumbnailData;
                            videoInfo.kind = kind;
                            videoInfo.width = width;
                            videoInfo.height = height;
                        } while (thumbnailCursor.moveToNext());

                        thumbnailCursor.close();
                    }

                    videoInfo.id = id;
                    videoInfo.data = data;
                    videoInfo.size = size;
                    videoInfo.displayName = displayName;
                    videoInfo.title = title;
                    videoInfo.dateAdded = dateAdded;
                    videoInfo.dateModified = dateModified;
                    videoInfo.mimeType = mimeType;
                    videoInfo.duration = duration;
                    videoInfo.artist = artist;
                    videoInfo.album = album;
                    videoInfo.resolution = resolution;
                    videoInfo.description = description;
                    videoInfo.isPrivate = isPrivate;
                    videoInfo.tags = tags;
                    videoInfo.category = category;
                    videoInfo.latitude = latitude;
                    videoInfo.longitude = longitude;
                    videoInfo.dateTaken = dateTaken;
                    videoInfo.miniThumbMagic = miniThumbMagic;
                    videoInfo.bucketId = bucketId;
                    videoInfo.bucketDisplayName = bucketDisplayName;
                    videoInfo.bookmark = bookmark;
                    mVideoInfos.add(videoInfo);
                } while (cursor.moveToNext());
                cursor.close();
                Log.e("wwh", "VideoActivity --> initFilms: " + mVideoInfos.size());
                dataBinding.start.post(() -> adapter.setList(mVideoInfos));
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.choose_video:
                dialog.show();
                break;
            case R.id.start:
                if (TextUtils.isEmpty(dataBinding.video.getFileName())) {
                    Toast.makeText(getApplicationContext(), "请选择视频", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (dataBinding.video.isDecoreDone()) {
                    dataBinding.video.start();
                } else {
                    Toast.makeText(getApplicationContext(), "视频还未加载完", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.pause:
                if (TextUtils.isEmpty(dataBinding.video.getFileName())) {
                    Toast.makeText(getApplicationContext(), "请选择视频", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (dataBinding.video.isDecoreDone()) {
                    dataBinding.video.pause();
                } else {
                    Toast.makeText(getApplicationContext(), "视频还未加载完", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
