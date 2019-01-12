package com.example.asus.weibo.profile;

import android.support.v4.app.Fragment;

import com.example.asus.weibo.SingleFragmentActivity;

public class ProfileActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return ProfileFragment.newInstance();
    }
}
