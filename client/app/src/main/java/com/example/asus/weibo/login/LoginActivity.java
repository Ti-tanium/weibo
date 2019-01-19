package com.example.asus.weibo.login;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.example.asus.weibo.R;
import com.example.asus.weibo.SingleFragmentActivity;

public class LoginActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return LoginFragment.newInstance();
    }

    @Override
    protected int getResourceId() {
        return R.layout.activity_fragment;
    }

    public static Intent newIntent(Context context){
        Intent intent=new Intent(context,LoginActivity.class);
        return intent;
    }
}
