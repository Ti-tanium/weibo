package com.example.asus.weibo.post;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.asus.weibo.HttpAgent;
import com.example.asus.weibo.Model.Weibo;
import com.example.asus.weibo.R;
import com.example.asus.weibo.Utils;
import com.example.asus.weibo.config;
import com.example.asus.weibo.login.LoginFragment;
import com.example.asus.weibo.weibolist.WeiboListFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.example.asus.weibo.Utils.getRealPathFromUri_AboveApi19;
import static com.example.asus.weibo.Utils.getRealPathFromUri_Api11To18;

@RuntimePermissions
public class PostFragment extends Fragment {
    private EditText mTitle;
    private EditText mDetail;
    private Button mPostBtn;
    private ImageView mPostImage;
    private String base64PostImage;
    private String mUserId;
    private Weibo mWeibo;
    public static String TAG="PostFragment";
    public static int PICK_IMAGE=1;
    private String imageRealPath;

    public static Fragment newInstance(){
        return new PostFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_post,container,false);
        mTitle=(EditText)view.findViewById(R.id.title);
        mDetail=(EditText)view.findViewById(R.id.detail);
        mPostImage=(ImageView)view.findViewById(R.id.detail_post_image);
        mPostBtn=(Button)view.findViewById(R.id.post_btn);
        mUserId=Utils.getPreference(getActivity()).getString(LoginFragment.KEY_LOGIN_ACCOUNT,null);
        mPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUserId==null){
                    Utils.showAlertDialog(getString(R.string.dialog_note),getString(R.string.dialog_login_note),getActivity()).show();
                }else{
                    new postTask().execute();
                }
            }
        });
        mPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),PICK_IMAGE);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                Log.i(TAG,"Result data is null");
                return;
            }
            Uri selectedImageUri = data.getData();
            mPostImage.setImageURI(selectedImageUri);
            Log.i(TAG,"URI:"+selectedImageUri);
            PostFragmentPermissionsDispatcher.getRealPathFromUriWithCheck(this,getActivity(),selectedImageUri);
            getRealPathFromUri(getActivity(),selectedImageUri);
            Log.i(TAG,"Post Image Path:"+imageRealPath);
            base64PostImage=Utils.imageToBase64(imageRealPath);
        }
    }

    /**
     * 根据图片的Uri获取图片的绝对路径(已经适配多种API)
     *
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void getRealPathFromUri(Context context, Uri uri) {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion < 11) {
            // SDK < Api11
            imageRealPath= Utils.getRealPathFromUri_BelowApi11(context, uri);
        }
        if (sdkVersion < 19) {
            // SDK > 11 && SDK < 19
            imageRealPath= getRealPathFromUri_Api11To18(context, uri);
        }
        // SDK > 19
        imageRealPath= getRealPathFromUri_AboveApi19(context, uri);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PostFragmentPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
    }

    private class postTask extends AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... voids) {
            mWeibo=new Weibo(mUserId,mTitle.getText().toString(),mDetail.getText().toString());
            WeiboListFragment.mWeibolist.add(mWeibo);
            Map<String,String>params=new HashMap<>();
            params.put("title",mWeibo.getTitle());
            params.put("detail",mWeibo.getDetail());
            params.put("posttime",mWeibo.getCreatedTime());
            Log.i(TAG,"Post Time:"+mWeibo.getCreatedTime());
            params.put("userid",mUserId);
            Log.i(TAG,"Base64 Image String:"+base64PostImage);
            params.put("weiboid",mWeibo.getWeiboid());
            if(base64PostImage!=null)
                params.put("image",base64PostImage);
            JSONObject responseJSON = HttpAgent.fetchJSON(config.request_post,params,"utf-8");
            if(responseJSON==null){
                return "-1";
            }
            String code=null;
            try{
                code=responseJSON.getString("code");
            }catch (JSONException je){
                Log.e(TAG,"Failed to get code from json:"+je);
            }
            return code;
        }

        @Override
        protected void onPostExecute(String s) {
            if(s.equals("1")){
                Toast.makeText(getActivity(),getString(R.string.suc_post_note),Toast.LENGTH_LONG).show();
            }else if(s.equals("-1")){
                Utils.showAlertDialog(getString(R.string.warning),getString(R.string.unreachable_server),getActivity()).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mUserId=Utils.getPreference(getActivity()).getString(LoginFragment.KEY_LOGIN_ACCOUNT,null);
    }
}
