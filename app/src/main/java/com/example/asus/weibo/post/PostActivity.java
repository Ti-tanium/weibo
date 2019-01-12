package com.example.asus.weibo.post;

import android.support.v4.app.Fragment;

import com.example.asus.weibo.SingleFragmentActivity;

public class PostActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return PostFragment.newInstance();
    }


}
