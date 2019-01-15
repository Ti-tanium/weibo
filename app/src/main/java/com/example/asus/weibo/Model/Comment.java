package com.example.asus.weibo.Model;

import com.example.asus.weibo.Utils;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;


public class Comment implements Serializable,Cloneable{
    /** 评论ID */
    private String commentId ;
    /** 评论者;评论者ID */
    private String commenter ;
    /** 点赞数 */
    private Integer thumpupCount ;
    /** 微博ID */
    private String weiboId ;
    /** 内容 **/
    private String content;
    // 评论时间
    private String commentTime;

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Comment(String commenterId, String pweiboId, String pcontent){
        commentId=UUID.randomUUID().toString();
        weiboId=pweiboId;
        content=pcontent;
        commenter=commenterId;
        commentTime=Utils.getNowDateTime();
    }
    public Comment(){

    }
    /** 评论ID */
    public String getCommentId(){
        return this.commentId;
    }
    /** 评论ID */
    public void setCommentId(String commentId){
        this.commentId = commentId;
    }
    /** 评论者;评论者ID */
    public String getCommenter(){
        return this.commenter;
    }
    /** 评论者;评论者ID */
    public void setCommenter(String commenter){
        this.commenter = commenter;
    }
    /** 点赞数 */
    public Integer getThumpupCount(){
        return this.thumpupCount;
    }
    /** 点赞数 */
    public void setThumpupCount(Integer thumpupCount){
        this.thumpupCount = thumpupCount;
    }
    /** 微博ID */
    public String getWeiboId(){
        return this.weiboId;
    }
    /** 微博ID */
    public void setWeiboId(String weiboId){
        this.weiboId = weiboId;
    }
}