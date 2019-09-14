package com.debin.wechat.snsad;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.debin.wechat.snsad.util.PrefUtil;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    private CheckBox wechatSnsAdCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wechatSnsAdCheckBox = (CheckBox) findViewById(R.id.cb_wechat_snsad);
        wechatSnsAdCheckBox.setOnCheckedChangeListener(this);

        init();
    }

    private void init() {
        if (MyApplication.getPrefUtil() != null) {
            PrefUtil prefUtil = MyApplication.getPrefUtil();
            wechatSnsAdCheckBox.setChecked(prefUtil.getBoolean(PrefUtil.REMOVE_WECHAT_SNSAD, true));
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (MyApplication.getPrefUtil() != null) {
            PrefUtil prefUtil = MyApplication.getPrefUtil();
            if (buttonView.getId() == wechatSnsAdCheckBox.getId()) {
                prefUtil.setBoolean(PrefUtil.REMOVE_WECHAT_SNSAD, isChecked);
            }
        } else {
            android.util.Log.e("onCheckedChanged", "MyApplication.getPrefUtil() return null");
        }
    }
}
