package com.yuugu.sample.module.share;

import android.content.Context;
import android.widget.Toast;

import com.yuugu.sample.framework.base.ShareService;

public class ShareServiceImpl implements ShareService {

    @Override
    public void share(Context context, String title, String message) {
        StringBuilder shareStr = new StringBuilder();
        shareStr
                .append("************* New Share *************").append("\n")
                .append("title: ").append(title).append("\n")
                .append("message: ").append(message).append("\n")
                .append("************* New Share *************");
        Toast.makeText(context, shareStr.toString(), Toast.LENGTH_LONG).show();
    }

}
