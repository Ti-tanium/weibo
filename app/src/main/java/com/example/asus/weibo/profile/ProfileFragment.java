package com.example.asus.weibo.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.weibo.R;
import com.example.asus.weibo.Utils;
import com.example.asus.weibo.login.LoginActivity;
import com.example.asus.weibo.login.LoginFragment;

public class ProfileFragment extends Fragment {
    private Button mLoginBtn;
    private ImageView mAvatar;
    private TextView mAccountId;
    private LinearLayout mMyPost;
    private String mAccount;
    private SharedPreferences mSharedPreferences;

    public static Fragment newInstance(){
        return new ProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_profile,container,false);
        mAvatar=(ImageView)view.findViewById(R.id.avatar);
        mAccountId=(TextView)view.findViewById(R.id.account);
        mMyPost=(LinearLayout)view.findViewById(R.id.mypost);
        mLoginBtn=(Button)view.findViewById(R.id.profile_login_btn);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAccount==null){
                    Intent intent=LoginActivity.newIntent(getActivity());
                    startActivity(intent);
                }else {
                    mSharedPreferences.edit().remove(LoginFragment.KEY_LOGIN_ACCOUNT).commit();
                    Toast.makeText(getActivity(),getString(R.string.log_out_toast),Toast.LENGTH_SHORT).show();
                    updateButton();
                }
            }
        });
        updateButton();
        return view;
    }

    public void updateButton(){
        // find out whether the user is logged in
        mSharedPreferences=Utils.getPreference(getActivity());
        mAccount=mSharedPreferences.getString(LoginFragment.KEY_LOGIN_ACCOUNT,null);
        if(mAccount!=null){
            mLoginBtn.setText(R.string.logout);
            mLoginBtn.setBackgroundColor(getResources().getColor(R.color.btn_light_red));
            mAccountId.setText(mAccount);
        }else{
            mLoginBtn.setText(R.string.login);
            mLoginBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mAccountId.setText(R.string.login_note);
            mAvatar.setImageDrawable(null);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        updateButton();
    }
}
