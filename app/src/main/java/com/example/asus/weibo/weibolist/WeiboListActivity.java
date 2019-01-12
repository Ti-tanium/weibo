package com.example.asus.weibo.weibolist;

import android.support.v4.app.Fragment;

import com.example.asus.weibo.SingleFragmentActivity;

public class WeiboListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return WeiboListFragment.newInstance();
    }
}
