package com.example.asus.weibo.weibodetail;

import android.content.ContentProvider;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaychan.viewlib.PowerfulEditText;
import com.example.asus.weibo.HttpAgent;
import com.example.asus.weibo.Model.Comment;
import com.example.asus.weibo.Model.Weibo;
import com.example.asus.weibo.R;
import com.example.asus.weibo.Utils;
import com.example.asus.weibo.config;
import com.example.asus.weibo.login.LoginFragment;
import com.example.asus.weibo.weibolist.WeiboListFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeiboDetailFragment extends Fragment {
    private ImageView mAvatar;
    private TextView mPostId;
    private TextView mPostTime;
    private TextView mTitle;
    private TextView mDetail;
    private ImageView mImage;
    private RecyclerView mRecyclerView;
    private EditText mComment;
    private Button mCommentSend;
    private CommentAdapter mCommentAdapter;
    private static List<Comment>mComments=new ArrayList<>();
    private Weibo mWeibo;
    private static String TAG="WeiboDetailFragment";
    private String userId;

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
        mComment=view.findViewById(R.id.weibo_detail_comment);
        mCommentSend=view.findViewById(R.id.weibo_detail_send);
        mRecyclerView=view.findViewById(R.id.detail_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Intent intent=getActivity().getIntent();
        mWeibo=(Weibo) intent.getSerializableExtra(WeiboDetailActivity.KEY_INTENT_EXTRA_WEIBO);
        Log.i(TAG,"Get Intent from Activity:"+mWeibo.getWeiboid());
        mPostId.setText(mWeibo.getPostid());
        mPostTime.setText(mWeibo.getCreatedTime());
        mTitle.setText(mWeibo.getTitle());
        mDetail.setText(mWeibo.getDetail());
        userId=Utils.getPreference(getActivity()).getString(LoginFragment.KEY_LOGIN_ACCOUNT,null);
        // EditText
        mCommentSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userId==null){
                    Utils.showAlertDialog(getString(R.string.alert),getString(R.string.dialog_login_note),getActivity());
                    return;
                }
                new postCommentTask().execute();
            }
        });
        // get comment list
        new getCommentListTask().execute();

        //TODO set avatar and image

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

    private class getCommentListTask extends AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... voids) {
            //TODO get data from server
            String url= config.get_comment+"?comment="+mWeibo.getComments();
            JSONObject jsonObject=HttpAgent.fetchJSON(url);
            if(jsonObject==null){
                return "-1";
            }
            parseCommentJSON(jsonObject);
            return "1";
        }

        private void parseCommentJSON(JSONObject jsonObject){
            try{
                JSONArray commentsArray=jsonObject.getJSONArray("comments");
                for(int i=0;i<commentsArray.length();i++){
                    JSONObject commentJSON=(JSONObject) commentsArray.get(i);
                    Comment comment=new Comment();
                    comment.setCommenter(commentJSON.getString("COMMENTER"));

                    comment.setCommentId(commentJSON.getString("COMMENT_ID"));
                    comment.setCommentTime(commentJSON.getString("COMMENT_TIME"));
                    comment.setContent(commentJSON.getString("CONTENT"));
                    comment.setWeiboId(commentJSON.getString("WEIBO_ID"));
                    mComments.add(comment);
                }
            }catch (JSONException je){
                Log.e(TAG,je.toString());
            }catch (ClassCastException cce){
                Log.e(TAG,cce.toString());
            }
        }

        @Override
        protected void onPostExecute(String code) {
            if(code.equals("-1")){
                Utils.showAlertDialog(getString(R.string.alert),getString(R.string.unreachable_server),getActivity());
                return;
            }
            updateUI();
        }
    }

    private class postCommentTask extends AsyncTask<Void,Void,String>{
        @Override
        protected String  doInBackground(Void... voids) {
            String commentString=mComment.getText().toString().trim();
            Comment comment=new Comment(userId,mWeibo.getWeiboid(),commentString);
            WeiboListFragment.commentWeibo(mWeibo.getWeiboid(),comment.getCommentId());
            mComments.add(comment);
            Map<String,String>params=new HashMap<>();
            params.put("comment",commentString);
            params.put("comment_time",comment.getCommentTime());
            params.put("commenterId",comment.getCommenter());
            params.put("commentId",comment.getCommentId());
            Log.i(TAG,"weiboId:"+comment.getWeiboId());
            params.put("weiboId",comment.getWeiboId());
            JSONObject jsonObject=HttpAgent.fetchJSON(config.post_comment,params,"utf-8");
            String code=null;
            try{
                code=jsonObject.getString("code");
            }catch (JSONException je){
                Log.e(TAG,"Failed to fetch String from JSON"+je);
            }
            return code;
        }

        @Override
        protected void onPostExecute(String code) {
            if(code.equals("1")){
                Toast.makeText(getActivity(),getString(R.string.success_post),Toast.LENGTH_SHORT).show();
                updateUI();
            }else{
                Utils.showAlertDialog(getString(R.string.alert),getString(R.string.unreachable_server),getActivity());
                return;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mComments.clear();
    }
}
