package com.example.asus.weibo.weibolist;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.ScrimInsetsFrameLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.weibo.HttpAgent;
import com.example.asus.weibo.Model.Weibo;
import com.example.asus.weibo.R;
import com.example.asus.weibo.config;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeiboListFragment extends Fragment {
    public static List<Weibo>mWeibolist=new ArrayList<>();
    private RecyclerView mRecyclerView;
    private WeiboAdapter mWeiboAdapter;
    private SmartRefreshLayout mSmartRefreshLayout;

    public static Fragment newInstance(){
        return new WeiboListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_weibolist,container,false);
        mRecyclerView=(RecyclerView)view.findViewById(R.id.recyclerview);

        // refresher
        mSmartRefreshLayout=(SmartRefreshLayout)view.findViewById(R.id.refreshLayout);
        mSmartRefreshLayout.setEnableRefresh(true);
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                new pullWeiboTask().execute();
            }
        });

        return view;
    }

    private void updateUI(){
        mWeiboAdapter=new WeiboAdapter(mWeibolist);
        mRecyclerView.setAdapter(mWeiboAdapter);
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

        public WeiboHolder(LayoutInflater inflater,ViewGroup parent){
            super(inflater.inflate(R.layout.card_weibo,parent,false));
            mAvatar=(ImageView)itemView.findViewById(R.id.avatar);
            mNickName=(TextView)itemView.findViewById(R.id.nickname);
            mPostTime=(TextView)itemView.findViewById(R.id.post_time);
            mTitle=(TextView)itemView.findViewById(R.id.title);
            mDetail=(TextView)itemView.findViewById(R.id.detail);
            mCommentCount=(TextView)itemView.findViewById(R.id.comment_count);
            mThumbupCount=(TextView)itemView.findViewById(R.id.thumbup_count);
        }

        public void bind(Weibo weibo){
            mWeibo=weibo;
            mNickName.setText(mWeibo.getPostid());
            mPostTime.setText(mWeibo.getCreatedTime().toString());
            mCommentCount.setText(mWeibo.getCommentCount());
            mThumbupCount.setText(mWeibo.getThumpupCount());
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

    private class pullWeiboTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            // TODO: pull data from server
            JSONObject responseObject=new HttpAgent().fetchJSON(config.get_weibo_list);
            parseWeiboListJSON(responseObject);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    public void parseWeiboListJSON(JSONObject jsonObject){

    }
}
