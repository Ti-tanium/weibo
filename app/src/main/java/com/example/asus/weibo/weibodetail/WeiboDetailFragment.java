package com.example.asus.weibo.weibodetail;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.weibo.Model.Comment;
import com.example.asus.weibo.Model.Weibo;
import com.example.asus.weibo.R;

import java.util.List;
public class WeiboDetailFragment extends Fragment {
    private ImageView mAvatar;
    private TextView mPostId;
    private TextView mPostTime;
    private TextView mTitle;
    private TextView mDetail;
    private ImageView mImage;
    private RecyclerView mRecyclerView;
    private CommentAdapter mCommentAdapter;
    private List<Comment>mComments;
    private Weibo mWeibo;
    private static String TAG="WeiboDetailFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_weibo_detail,container,false);
        mAvatar=view.findViewById(R.id.detail_avatar);
        mPostId=view.findViewById(R.id.detail_postid);
        mPostTime=view.findViewById(R.id.detail_post_time);
        mTitle=view.findViewById(R.id.detail_title);
        mDetail=view.findViewById(R.id.detail_weibo_detail);
        mImage=view.findViewById(R.id.detail_post_image);
        mRecyclerView=view.findViewById(R.id.detail_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Intent intent=getActivity().getIntent();
        mWeibo=(Weibo) intent.getSerializableExtra(WeiboDetailActivity.KEY_INTENT_EXTRA_WEIBO);
        Log.i(TAG,"Get Intent from Activity:"+mWeibo.getWeiboid());
        mPostId.setText(mWeibo.getPostid());
        mPostTime.setText(mWeibo.getCreatedTime());
        mTitle.setText(mWeibo.getTitle());
        mDetail.setText(mWeibo.getDetail());
        //TODO set avatar

        return view;
    }

    public void updateUI(){
        mCommentAdapter=new CommentAdapter(mComments);
        mRecyclerView.setAdapter(mCommentAdapter);
    }

    public static Fragment newInstance(){
        return new WeiboDetailFragment();
    }

    private class CommentHolder extends RecyclerView.ViewHolder{
        private Comment mComment;
        private ImageView avatar;
        private TextView posterId;
        private TextView postTime;
        private TextView content;

        public CommentHolder(LayoutInflater inflater,ViewGroup parent){
            super(inflater.inflate(R.layout.item_comment,parent,false));
            avatar=itemView.findViewById(R.id.comment_avatar);
            posterId=itemView.findViewById(R.id.comment_postid);
            postTime=itemView.findViewById(R.id.comment_time);
            content=itemView.findViewById(R.id.comment_content);
        }

        public void bind(Comment comment){
            posterId.setText(comment.getCommenter());
            postTime.setText(comment.getCommentTime());
            content.setText(comment.getContent());
        }
    }

    private class CommentAdapter extends RecyclerView.Adapter<CommentHolder>{
        List<Comment>mComments;
        @Override
        public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
            Comment comment=mComments.get(position);
            holder.bind(comment);
        }

        public CommentAdapter(List<Comment>comments){
            mComments=comments;
        }

        @Override
        public int getItemCount() {
            return mComments.size();
        }

        @NonNull
        @Override
        public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater=LayoutInflater.from(getActivity());
            return new CommentHolder(inflater,parent);
        }
    }

    private class getCommentListTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            //TODO get data from server
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateUI();
        }
    }
}
