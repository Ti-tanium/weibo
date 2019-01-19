package com.example.asus.weibo.weibolist;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.weibo.HttpAgent;
import com.example.asus.weibo.Model.Weibo;
import com.example.asus.weibo.R;
import com.example.asus.weibo.Utils;
import com.example.asus.weibo.config;
import com.example.asus.weibo.weibodetail.WeiboDetailActivity;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class WeiboListFragment extends Fragment {
    private static final String TAG ="WeiboListFragment";
    public static List<Weibo>mWeibolist=new ArrayList<>();
    private RecyclerView mRecyclerView;
    private WeiboAdapter mWeiboAdapter;
    private SmartRefreshLayout mSmartRefreshLayout;

    public static Fragment newInstance(){
        return new WeiboListFragment();
    }

    public static void commentWeibo(String weiboid,String commentId){
        for (Weibo w :mWeibolist){
            if(w.getWeiboid().equals(weiboid)){
                String old=w.getComments();
                if(old==""){
                    w.setComments(commentId);
                }else {
                    String newComment = old.concat("," + commentId);
                    w.setComments(newComment);
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        View view=inflater.inflate(R.layout.fragment_weibolist,container,false);
        mRecyclerView=view.findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // refresher
        mSmartRefreshLayout=(SmartRefreshLayout)view.findViewById(R.id.refreshLayout);
        mSmartRefreshLayout.setEnableRefresh(true);
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                new pullWeiboTask().execute();
                refreshLayout.finishRefresh();
            }
        });
        new pullWeiboTask().execute();
        return view;
    }

    private void updateUI(){
        mWeiboAdapter=new WeiboAdapter(mWeibolist);
        mRecyclerView.setAdapter(mWeiboAdapter);
        Log.i(TAG,"Update UI Done.");
    }

    private class WeiboHolder extends RecyclerView.ViewHolder{
        private Weibo mWeibo;
        private ImageView mAvatar;
        private TextView mNickName;
        private TextView mPostTime;
        private TextView mTitle;
        private TextView mDetail;
        private TextView mCommentCount;
        private TextView mThumbupCount;
        private CardView mCardView;
        private ShineButton mHeartBtn;

        public WeiboHolder(LayoutInflater inflater,ViewGroup parent){
            super(inflater.inflate(R.layout.card_weibo,parent,false));
            mAvatar=(ImageView)itemView.findViewById(R.id.card_avatar);
            mNickName=(TextView)itemView.findViewById(R.id.card_nickname);
            mPostTime=(TextView)itemView.findViewById(R.id.card_post_time);
            mTitle=(TextView)itemView.findViewById(R.id.card_title);
            mDetail=(TextView)itemView.findViewById(R.id.card_view_detail);
            mCommentCount=(TextView)itemView.findViewById(R.id.card_comment_count);
            mThumbupCount=(TextView)itemView.findViewById(R.id.card_thumbup_count);
            mCardView=itemView.findViewById(R.id.cardview);
            mHeartBtn=itemView.findViewById(R.id.card_heart_btn);
            mHeartBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO fake thumb up , haven't syncing with server
                    mThumbupCount.setText("1");
                }
            });
            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(WeiboDetailActivity.newIntent(getActivity(),mWeibo));
                }
            });
        }

        public void bind(Weibo weibo){
            mWeibo=weibo;
            mNickName.setText(mWeibo.getPostid());
            mPostTime.setText(mWeibo.getCreatedTime());
            mCommentCount.setText(mWeibo.getCommentCount());
            mThumbupCount.setText(mWeibo.getThumbupCountString());
            mTitle.setText(mWeibo.getTitle());
            mDetail.setText(mWeibo.getDetail());
        }
    }

    private class WeiboAdapter extends RecyclerView.Adapter<WeiboHolder>{
        private List<Weibo>mWeibos;

        public WeiboAdapter(List<Weibo> weibos){
            mWeibos=weibos;
        }

        @Override
        public int getItemCount() {
            return mWeibos.size();
        }

        @Override
        public void onBindViewHolder(@NonNull WeiboHolder holder, int position) {
            Weibo weibo=mWeibos.get(position);
            holder.bind(weibo);
        }

        @NonNull
        @Override
        public WeiboHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater=LayoutInflater.from(getActivity());
            return new WeiboHolder(layoutInflater,parent);
        }
    }

    private class pullWeiboTask extends AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... voids) {
            Log.i(TAG,"Start Pulling Data from server.");
            JSONObject responseObject=new HttpAgent().fetchJSON(config.get_weibo_list);
            String code=parseWeiboListJSON(responseObject);
            return code;
        }

        @Override
        protected void onPostExecute(String code) {
            if(code=="-1"){
                Utils.showAlertDialog(getString(R.string.alert),getString(R.string.network_err_alert),getActivity()).show();
                return;
            }
            updateUI();
        }
    }

    public String parseWeiboListJSON(JSONObject jsonObject){
        try{
            mWeibolist.clear();
            if(jsonObject==null){
                return "-1";
            }
            JSONArray weibolist=jsonObject.getJSONArray("sql");
            for(int i=0;i<weibolist.length();i++){
                JSONObject weiboJSON=(JSONObject) weibolist.get(i);
                Weibo weibo=new Weibo();
                weibo.setTitle(weiboJSON.getString("TITLE"));
                weibo.setDetail(weiboJSON.getString("DETAIL"));
                weibo.setThumpupCount(weiboJSON.getInt("THUMBUP_COUNT"));
                weibo.setComments(weiboJSON.getString("COMMENTS"));
                weibo.setPostid(weiboJSON.getString("POSTID"));
                weibo.setWeiboid(weiboJSON.getString("WEIBOID"));
                weibo.setCreatedTime(weiboJSON.getString("CREATED_TIME"));
                mWeibolist.add(weibo);
            }
            return "1";
        }catch (JSONException je){
            Log.e(TAG,"Failed to get sql from JSON"+je.fillInStackTrace());
            return "-1";
        }
    }


}
