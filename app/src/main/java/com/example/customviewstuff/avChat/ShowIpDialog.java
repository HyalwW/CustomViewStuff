package com.example.customviewstuff.avChat;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.customviewstuff.R;
import com.uuzuche.lib_zxing.activity.CodeUtils;

public class ShowIpDialog extends AlertDialog {
    private ImageView qrImage;
    private TextView ipTextView;

    public ShowIpDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_show_ip);
        getWindow().setBackgroundDrawable(null);
        qrImage = findViewById(R.id.qr_img);
        ipTextView = findViewById(R.id.ip);
    }

    public void show(String ipt) {
        show();
        ipTextView.setText(ipt);
        qrImage.setImageBitmap(CodeUtils.createImage(ipt, dp2px(200), dp2px(200), null));
    }

    public int dp2px(float dipValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
