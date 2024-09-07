package com.example.share;

import com.example.share.activity.ImageInfo;
import com.google.gson.annotations.SerializedName;

import java.util.List;
public class ShareItem {
    private int current; // 当前页
    private List<ImageInfo> records; // 数据列表
    private long collectId; // 当前图片分享的用户收藏的主键id
    private int collectNum; // 当前图片分享的收藏数
    private String content; // 内容
    private long createTime; // 创建时间
    private boolean hasCollect; // 是否已收藏
    private boolean hasFocus; // 是否已关注
    private boolean hasLike; // 是否已点赞
    private long id; // 主键id
    private long imageCode; // 一组图片的唯一标识符
    private List<String> imageUrlList; // 图片的list集合
    private long likeId; // 当前图片分享的用户点赞的主键id
    private int likeNum; // 当前图片分享的点赞数
    private long pUserId; // 当前登录用户(发布者)id
    private String title; // 标题
    private String username; // 当前登录用户名
    private int size; // 页面大小
    private long total; // 共多少条

    // 构造方法、getter 和 setter

    public ShareItem() {}

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public List<ImageInfo> getRecords() {
        return records;
    }

    public void setRecords(List<ImageInfo> records) {
        this.records = records;
    }

    public long getCollectId() {
        return collectId;
    }

    public void setCollectId(long collectId) {
        this.collectId = collectId;
    }

    public int getCollectNum() {
        return collectNum;
    }

    public void setCollectNum(int collectNum) {
        this.collectNum = collectNum;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public boolean isHasCollect() {
        return hasCollect;
    }

    public void setHasCollect(boolean hasCollect) {
        this.hasCollect = hasCollect;
    }

    public boolean isHasFocus() {
        return hasFocus;
    }

    public void setHasFocus(boolean hasFocus) {
        this.hasFocus = hasFocus;
    }

    public boolean isHasLike() {
        return hasLike;
    }

    public void setHasLike(boolean hasLike) {
        this.hasLike = hasLike;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getImageCode() {
        return imageCode;
    }

    public void setImageCode(long imageCode) {
        this.imageCode = imageCode;
    }

    public List<String> getImageUrlList() {
        return imageUrlList;
    }

    public void setImageUrlList(List<String> imageUrlList) {
        this.imageUrlList = imageUrlList;
    }

    public long getLikeId() {
        return likeId;
    }

    public void setLikeId(long likeId) {
        this.likeId = likeId;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public long getpUserId() {
        return pUserId;
    }

    public void setpUserId(long pUserId) {
        this.pUserId = pUserId;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}