package com.example.customviewstuff.customs.eventDispatch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.customviewstuff.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wang.Wenhui
 * Date: 2020/4/16
 */
public class SetAdapter extends BaseAdapter {
    private List<Bean> list;
    private LayoutInflater inflate;
    private OnReturnTypeChangedListener listener;

    public SetAdapter(Context context) {
        list = new ArrayList<>();
        inflate = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Bean getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflate.inflate(R.layout.set_item, null);
            holder = new ViewHolder();
            holder.name = convertView.findViewById(R.id.name);
            holder.chooser = convertView.findViewById(R.id.chooser);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.bind(list.get(position));
        return convertView;
    }

    private class ViewHolder {
        TextView name;
        RadioGroup chooser;

        public void bind(Bean bean) {
            name.setText(bean.name);
            chooser.check(getId(bean.type));
            chooser.setOnCheckedChangeListener((group, checkedId) -> {
                switch (checkedId) {
                    case R.id.item_false:
                        bean.type = ReturnType.FALSE;
                        break;
                    case R.id.item_true:
                        bean.type = ReturnType.TRUE;
                        break;
                    case R.id.item_super:
                    default:
                        bean.type = ReturnType.SUPER;
                        break;
                }
                if (listener != null) {
                    listener.onTypeChanged(bean.name, bean.type);
                }
            });
        }
    }

    private int getId(ReturnType type) {
        switch (type) {
            case FALSE:
                return R.id.item_false;
            case TRUE:
                return R.id.item_true;
            case SUPER:
            default:
                return R.id.item_super;
        }
    }

    public static class Bean {
        String name;
        ReturnType type;

        public Bean(String name, ReturnType type) {
            this.name = name;
            this.type = type;
        }
    }

    public void setListener(OnReturnTypeChangedListener listener) {
        this.listener = listener;
    }

    public void setList(List<Bean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public interface OnReturnTypeChangedListener {
        void onTypeChanged(String name, ReturnType type);
    }
}
