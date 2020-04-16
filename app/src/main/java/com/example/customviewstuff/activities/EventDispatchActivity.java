package com.example.customviewstuff.activities;

import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import com.example.customviewstuff.BaseActivity;
import com.example.customviewstuff.R;
import com.example.customviewstuff.customs.eventDispatch.EventDispatchView;
import com.example.customviewstuff.customs.eventDispatch.OnEventActive;
import com.example.customviewstuff.customs.eventDispatch.OnTypeChanged;
import com.example.customviewstuff.customs.eventDispatch.ReturnType;
import com.example.customviewstuff.customs.eventDispatch.SetAdapter;
import com.example.customviewstuff.databinding.ActivityEventDispatchBinding;

import java.util.ArrayList;
import java.util.List;

public class EventDispatchActivity extends BaseActivity<ActivityEventDispatchBinding> implements OnTypeChanged {
    private ReturnType dispatchType, onTouchType;
    private AlertDialog dialog;


    @Override
    protected int layoutId() {
        return R.layout.activity_event_dispatch;
    }

    @Override
    protected void onInit() {
        dispatchType = ReturnType.SUPER;
        onTouchType = ReturnType.SUPER;
        dataBinding.viewGroup.setActive(new OnEventActive() {
            @Override
            public void onDispatchActive() {
                dataBinding.drawView.active(EventDispatchView.VGD);
            }

            @Override
            public void onInterceptActive() {
                dataBinding.drawView.active(EventDispatchView.VGI);
            }

            @Override
            public void onTouchActive() {
                dataBinding.drawView.active(EventDispatchView.VGO);
            }
        });
        dataBinding.view.setActive(new OnEventActive() {
            @Override
            public void onDispatchActive() {
                dataBinding.drawView.active(EventDispatchView.VD);
            }

            @Override
            public void onInterceptActive() {

            }

            @Override
            public void onTouchActive() {
                dataBinding.drawView.active(EventDispatchView.VO);
            }
        });
        initDialog();
        dataBinding.setBtn.setOnClickListener(v -> dialog.show());
    }

    private void initDialog() {
        SetAdapter adapter = new SetAdapter(this);
        List<SetAdapter.Bean> list = getList();
        adapter.setList(list);
        adapter.setListener((name, type) -> {
            dataBinding.drawView.resetType(name, type);
            switch (name) {
                case EventDispatchView.AD:
                case EventDispatchView.AO:
                    onTypeChanged(name, type);
                    break;
                case EventDispatchView.VGD:
                case EventDispatchView.VGI:
                case EventDispatchView.VGO:
                    dataBinding.viewGroup.onTypeChanged(name, type);
                    break;
                case EventDispatchView.VD:
                case EventDispatchView.VO:
                    dataBinding.view.onTypeChanged(name, type);
                    break;
            }
        });
        View view = LayoutInflater.from(this).inflate(R.layout.dialog, null);
        dialog = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(true)
                .create();
        ListView listView = view.findViewById(R.id.list_view);
        listView.setAdapter(adapter);
    }

    private List<SetAdapter.Bean> getList() {
        List<SetAdapter.Bean> list = new ArrayList<>();
        list.add(new SetAdapter.Bean(EventDispatchView.AD, ReturnType.SUPER));
        list.add(new SetAdapter.Bean(EventDispatchView.AO, ReturnType.SUPER));
        list.add(new SetAdapter.Bean(EventDispatchView.VGD, ReturnType.SUPER));
        list.add(new SetAdapter.Bean(EventDispatchView.VGI, ReturnType.SUPER));
        list.add(new SetAdapter.Bean(EventDispatchView.VGO, ReturnType.SUPER));
        list.add(new SetAdapter.Bean(EventDispatchView.VD, ReturnType.SUPER));
        list.add(new SetAdapter.Bean(EventDispatchView.VO, ReturnType.SUPER));
        return list;
    }

    private float lx, ly;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getY() >= dataBinding.drawView.getTop()) {
            return super.dispatchTouchEvent(ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dataBinding.drawView.action("ACTION_DOWN");
                lx = ev.getRawX();
                ly = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (distance(ev.getRawX(), ev.getRawY(), lx, ly) >= 15) {
                    dataBinding.drawView.action("ACTION_MOVE");
                    lx = ev.getRawX();
                    ly = ev.getRawY();
                } else {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                dataBinding.drawView.action("ACTION_UP");
                break;
        }
        dataBinding.drawView.active(EventDispatchView.AD);
        switch (dispatchType) {
            case TRUE:
                return true;
            case FALSE:
                return false;
            case SUPER:
            default:
                return super.dispatchTouchEvent(ev);
        }
    }

    private float distance(float sx, float sy, float ex, float ey) {
        return (float) Math.sqrt((sx - ex) * (sx - ex) + (sy - ey) * (sy - ey));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getY() >= dataBinding.drawView.getTop()) {
            return super.onTouchEvent(event);
        }
        dataBinding.drawView.active(EventDispatchView.AO);
        switch (onTouchType) {
            case TRUE:
                return true;
            case FALSE:
                return false;
            case SUPER:
            default:
                return super.onTouchEvent(event);
        }
    }

    @Override
    public void onTypeChanged(String name, ReturnType type) {
        switch (name) {
            case EventDispatchView.AD:
                dispatchType = type;
                break;
            case EventDispatchView.AO:
                onTouchType = type;
                break;
        }
    }
}
