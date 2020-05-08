package com.example.customviewstuff.activities.soccer;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.EditText;

import com.example.customviewstuff.R;
import com.example.customviewstuff.customs.DnmButton;

public class IpDialog extends AlertDialog {
    private EditText edit;
    private DnmButton ok;
    private OnIpAddrCheckListener listener;

    protected IpDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ip_input);
        edit = findViewById(R.id.edit);
        ok = findViewById(R.id.ok);
        ok.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(edit.getText())) {
                if (listener != null) {
                    listener.onCheck(edit.getText().toString());
                }
                dismiss();
            }
        });
        setCanceledOnTouchOutside(false);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }

    public void setListener(OnIpAddrCheckListener listener) {
        this.listener = listener;
    }

    public interface OnIpAddrCheckListener {
        void onCheck(String ip);
    }
}
