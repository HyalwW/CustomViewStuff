package com.example.customviewstuff.videoAbout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.customviewstuff.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wang.Wenhui
 * Date: 2020/1/15
 */
public class FilmAdapter extends BaseAdapter {
    private Context context;
    private List<VideoInfo> list;
    private LayoutInflater inflate;

    public FilmAdapter(Context context) {
        inflate = LayoutInflater.from(context);
        list = new ArrayList<>();
        this.context = context;
    }

    public void setList(List<VideoInfo> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflate.inflate(R.layout.video_item, null);
            holder = new ViewHolder();
            holder.serial = convertView.findViewById(R.id.serial);
            holder.title = convertView.findViewById(R.id.title);
            holder.duration = convertView.findViewById(R.id.duration);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.bind(list.get(position));
        return convertView;
    }

    private class ViewHolder {
        TextView serial, title, duration;

        public void bind(VideoInfo info) {
            serial.setText(String.valueOf(list.indexOf(info) + 1));
            title.setText(info.displayName);
            duration.setText(String.valueOf(info.duration));
        }
    }
}
