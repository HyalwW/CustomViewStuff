package com.example.customviewstuff.videoAbout;

/**
 * Created by Wang.Wenhui
 * Date: 2020/1/14
 */
public class VideoInfo {
    public int id;
    public String data;
    public long size;
    public String displayName;
    public String title;
    public long dateAdded;
    public long dateModified;
    public String mimeType;
    public long duration;
    public String artist;
    public String album;
    public String resolution;
    public String description;
    public int isPrivate;
    public String tags;
    public String category;
    public double latitude;
    public double longitude;
    public int dateTaken;
    public int miniThumbMagic;
    public String bucketId;
    public String bucketDisplayName;
    public int bookmark;
    public String thumbnailData;
    public int kind;
    public long width;
    public long height;

    @Override
    public String toString() {
        return "VideoInfo{" +
                "id=" + id +
                ", data='" + data + '\'' +
                ", size=" + size +
                ", displayName='" + displayName + '\'' +
                ", title='" + title + '\'' +
                ", dateAdded=" + dateAdded +
                ", dateModified=" + dateModified +
                ", mimeType='" + mimeType + '\'' +
                ", duration=" + duration +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", resolution='" + resolution + '\'' +
                ", description='" + description + '\'' +
                ", isPrivate=" + isPrivate +
                ", tags='" + tags + '\'' +
                ", category='" + category + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", dateTaken=" + dateTaken +
                ", miniThumbMagic=" + miniThumbMagic +
                ", bucketId='" + bucketId + '\'' +
                ", bucketDisplayName='" + bucketDisplayName + '\'' +
                ", bookmark=" + bookmark +
                ", thumbnailData='" + thumbnailData + '\'' +
                ", kind=" + kind +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
