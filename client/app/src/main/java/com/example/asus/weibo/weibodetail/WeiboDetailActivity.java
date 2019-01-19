package com.example.asus.weibo.weibodetail;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.example.asus.weibo.Model.Weibo;
import com.example.asus.weibo.R;
import com.example.asus.weibo.SingleFragmentActivity;

public class WeiboDetailActivity extends SingleFragmentActivity {
    public static String KEY_INTENT_EXTRA_WEIBO;

    @Override
    protected Fragment createFragment() {
        return WeiboDetailFragment.newInstance();
    }

    @Override
    protected int getResourceId() {
        return R.layout.activity_fragment;
    }

    public static Intent newIntent(Context context, Weibo weibo){
        Intent intent=new Intent(context,WeiboDetailActivity.class);
        intent.putExtra(KEY_INTENT_EXTRA_WEIBO,weibo);
        return intent;
    }
}
