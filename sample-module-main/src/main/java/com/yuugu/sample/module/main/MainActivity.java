package com.yuugu.sample.module.main;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yuugu.modular.dispatcher.core.ModularDispatcher;
import com.yuugu.sample.framework.base.ShareService;

public class MainActivity extends AppCompatActivity {

    private Button btn_share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_share = findViewById(R.id.btn_share);
        btn_share.setOnClickListener(view -> {
            ShareService shareService = ModularDispatcher.get().service(ShareService.class);
            shareService.share(this, "会议标题", "会议内容");
        });
    }
}