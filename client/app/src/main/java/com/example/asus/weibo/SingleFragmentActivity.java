package com.example.asus.weibo;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.asus.weibo.post.PostFragment;
import com.example.asus.weibo.profile.ProfileFragment;
import com.example.asus.weibo.weibolist.WeiboListFragment;

public abstract class SingleFragmentActivity extends AppCompatActivity {

    final Fragment homeFragment=WeiboListFragment.newInstance();
    final Fragment postFragment=PostFragment.newInstance();
    final Fragment profileFragment=ProfileFragment.newInstance();
    final FragmentManager fm = getSupportFragmentManager();
    private BottomNavigationView mBottomNavigationView;
    Fragment activeFragment = homeFragment;


    protected abstract Fragment createFragment();

    @LayoutRes
    protected int getResourceId(){
        return R.layout.activity_fragment_navigation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResourceId());

        if(getResourceId()==R.layout.activity_fragment_navigation){
            initWithNavigation();
        }else {
            Fragment fragment = fm.findFragmentById(R.id.fragment_container);
            if (fragment == null) {
                fragment = createFragment();
                fm.beginTransaction()
                        .add(R.id.fragment_container, fragment)
                        .commit();
            }
        }
    }

    private void initWithNavigation(){
        fm.beginTransaction().add(R.id.fragment_container, profileFragment, "3").hide(profileFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, postFragment, "2").hide(postFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container,homeFragment, "1").commit();
        mBottomNavigationView=(BottomNavigationView)findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setEnabled(true);
                switch (item.getItemId()){
                    case R.id.bottom_nav_home:
                        fm.beginTransaction().hide(activeFragment).show(homeFragment).commit();
                        activeFragment=homeFragment;
                        return true;

                    case R.id.bottom_nav_post:
                        fm.beginTransaction().hide(activeFragment).show(postFragment).commit();
                        activeFragment=postFragment;
                        return true;

                    case R.id.bottom_nav_profile:
                        fm.beginTransaction().hide(activeFragment).show(profileFragment).commit();
                        activeFragment=profileFragment;
                        return true;
                }
                return false;
            }
        });
    }

}
