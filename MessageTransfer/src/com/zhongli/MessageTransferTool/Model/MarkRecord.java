package com.zhongli.MessageTransferTool.Model;

import java.util.Date;
import java.util.List;

/**
 * 存储一条标注的记录
 * 
 * @author zhonglili
 *
 */
public class MarkRecord {
	// 记录编号
	private long record_id;
	// 消息编号
	private long msg_id;
	// 标记者编号
	private long user_id;
	// 标记时间
	private Date mark_at;
	// 标记文字
	private String text;
	// 文字情感
	private String emotion_text;
	// 媒体类型
	private List<String> media_types;
	// 媒体url
	private List<String> media_urls;
	// 媒体本地url
	private List<String> media_urls_local;
	// 标记的媒体的情感
	private List<String> emotion_medias;
	public long getRecord_id() {
		return record_id;
	}
	public void setRecord_id(long record_id) {
		this.record_id = record_id;
	}
	public long getMsg_id() {
		return msg_id;
	}
	public void setMsg_id(long msg_id) {
		this.msg_id = msg_id;
	}
	public long getUser_id() {
		return user_id;
	}
	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}
	public Date getMark_at() {
		return mark_at;
	}
	public void setMark_at(Date mark_at) {
		this.mark_at = mark_at;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getEmotion_text() {
		return emotion_text;
	}
	public void setEmotion_text(String emotion_text) {
		this.emotion_text = emotion_text;
	}
	public List<String> getMedia_types() {
		return media_types;
	}
	public void setMedia_types(List<String> media_types) {
		this.media_types = media_types;
	}
	public List<String> getMedia_urls() {
		return media_urls;
	}
	public void setMedia_urls(List<String> media_urls) {
		this.media_urls = media_urls;
	}
	public List<String> getMedia_urls_local() {
		return media_urls_local;
	}
	public void setMedia_urls_local(List<String> media_urls_local) {
		this.media_urls_local = media_urls_local;
	}
	public List<String> getEmotion_medias() {
		return emotion_medias;
	}
	public void setEmotion_medias(List<String> emotion_medias) {
		this.emotion_medias = emotion_medias;
	}
	@Override
	public String toString() {
		return "MarkRecord [record_id=" + record_id + ", msg_id=" + msg_id
				+ ", user_id=" + user_id + ", mark_at=" + mark_at + ", text="
				+ text + ", emotion_text=" + emotion_text + ", media_types="
				+ media_types + ", media_urls=" + media_urls
				+ ", media_urls_local=" + media_urls_local
				+ ", emotion_medias=" + emotion_medias + "]";
	}
	

}
