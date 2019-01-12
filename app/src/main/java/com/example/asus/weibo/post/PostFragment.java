package com.example.asus.weibo.post;

import android.app.Activity;
import android.content.Intent;
import android.nfc.Tag;
import android.os.AsyncTask;
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

import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class PostFragment extends Fragment {
    private EditText mTitle;
    private EditText mDetail;
    private Button mPostBtn;
    private ImageView mPostImage;
    private String mUserId;
    private Weibo mWeibo;
    public static String TAG="PostFragment";
    public static int PICK_IMAGE=1;

    public static Fragment newInstance(){
        return new PostFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_post,container,false);
        mTitle=(EditText)view.findViewById(R.id.title);
        mDetail=(EditText)view.findViewById(R.id.detail);
        mPostImage=(ImageView)view.findViewById(R.id.post_image);
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
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                Log.e(TAG,"Result data is null");
                return;
            }
            try{
                ByteArrayOutputStream out=new ByteArrayOutputStream();
                InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                int bytesRead = 0;
                byte[] buffer = new byte[1024];
                while ((bytesRead = inputStream.read(buffer)) > 0) {
                    out.write(buffer, 0, bytesRead);
                }
                out.close();
                Log.i(TAG,out.toByteArray().toString());
                //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
            }catch (IOException ioe){
                Log.e(TAG,"Failed to get input stream:"+ioe);
            }
        }
    }

    private class postTask extends AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... voids) {
            mWeibo=new Weibo(mUserId,mTitle.getText().toString(),mDetail.getText().toString());
            WeiboListFragment.mWeibolist.add(mWeibo);
            Map<String,String>params=new HashMap<>();
            params.put("title",mWeibo.getTitle());
            params.put("detail",mWeibo.getDetail());
            params.put("posttime",mWeibo.getCreatedTime().toString());
            params.put("userid",mUserId);
            params.put("weiboid",mWeibo.getWeiboid());
            Log.i(TAG,mWeibo.getDetail());
            JSONObject responseJSON = HttpAgent.fetchJSON(config.request_post,params,"utf-8");
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
