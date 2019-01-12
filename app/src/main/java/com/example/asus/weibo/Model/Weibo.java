package com.example.asus.weibo.Model;

import com.example.asus.weibo.Utils;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Weibo implements Serializable,Cloneable{
    /** 乐观锁 */
    private Integer revision ;
    /** 创建人 */
    private String createdBy ;
    /** 创建时间 */
    private String createdTime ;
    /** 更新人 */
    private String updatedBy ;
    /** 更新时间 */
    private Date updatedTime ;
    /** 发布人ID */
    private String postid ;
    /** 微博ID */
    private String weiboid ;
    /** 点赞数 */
    private int thumpupCount ;
    /** 评论;评论ID数组 */
    private String comments;
    /** 转发数 */
    private int forwardCount ;
    /** 标题 */
    private String title ;
    /** 详细内容 */
    private String detail ;
    /** 图片 */
    private String image ;

    public Weibo(String posterId,String stitle,String sdetail){
        createdTime=Utils.getNowDateTime();
        weiboid=UUID.randomUUID().toString();
        postid=posterId;
        thumpupCount=0;
        comments="";
        forwardCount=0;
        title=stitle;
        detail=sdetail;

    }

    public int getCommentCount(){
        String[] a=comments.split("`");
        return a.length;
    }

    /** 乐观锁 */
    public Integer getRevision(){
        return this.revision;
    }
    /** 乐观锁 */
    public void setRevision(Integer revision){
        this.revision = revision;
    }
    /** 创建人 */
    public String getCreatedBy(){
        return this.createdBy;
    }
    /** 创建人 */
    public void setCreatedBy(String createdBy){
        this.createdBy = createdBy;
    }
    /** 创建时间 */
    public String  getCreatedTime(){
        return this.createdTime;
    }
    /** 创建时间 */
    public void setCreatedTime(String createdTime){
        this.createdTime = createdTime;
    }
    /** 更新人 */
    public String getUpdatedBy(){
        return this.updatedBy;
    }
    /** 更新人 */
    public void setUpdatedBy(String updatedBy){
        this.updatedBy = updatedBy;
    }
    /** 更新时间 */
    public Date getUpdatedTime(){
        return this.updatedTime;
    }
    /** 更新时间 */
    public void setUpdatedTime(Date updatedTime){
        this.updatedTime = updatedTime;
    }
    /** 发布人ID */
    public String getPostid(){
        return this.postid;
    }
    /** 发布人ID */
    public void setPostid(String postid){
        this.postid = postid;
    }
    /** 微博ID */
    public String getWeiboid(){
        return this.weiboid;
    }
    /** 微博ID */
    public void setWeiboid(String weiboid){
        this.weiboid = weiboid;
    }
    /** 点赞数 */
    public Integer getThumpupCount(){
        return this.thumpupCount;
    }
    /** 点赞数 */
    public void setThumpupCount(Integer thumpupCount){
        this.thumpupCount = thumpupCount;
    }
    /** 评论;评论ID数组 */
    public String getComments(){
        return this.comments;
    }
    /** 评论;评论ID数组 */
    public void setComments(String comments){
        this.comments = comments;
    }
    /** 转发数 */
    public Integer getForwardCount(){
        return this.forwardCount;
    }
    /** 转发数 */
    public void setForwardCount(Integer forwardCount){
        this.forwardCount = forwardCount;
    }
    /** 标题 */
    public String getTitle(){
        return this.title;
    }
    /** 标题 */
    public void setTitle(String title){
        this.title = title;
    }
    /** 详细内容 */
    public String getDetail(){
        return this.detail;
    }
    /** 详细内容 */
    public void setDetail(String detail){
        this.detail = detail;
    }
    /** 图片 */
    public String getImage(){
        return this.image;
    }
    /** 图片 */
    public void setImage(String image){
        this.image = image;
    }
}
